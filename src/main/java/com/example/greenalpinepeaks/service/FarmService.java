package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Activity;
import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.FarmImage;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmEditDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmSearchCriteria;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.mapper.ActivityMapper;
import com.example.greenalpinepeaks.mapper.FarmMapper;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.ActivityRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.FarmImageRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FarmService {

    private static final String FARM_NOT_FOUND = "Farm not found: ";
    private static final String REGION_REQUIRED_MESSAGE = "Region is required";

    @Value("${farm.images.upload-dir:uploads/farms}")
    private String UPLOAD_DIR;

    private final ActivityRepository activityRepository;
    private final FarmRepository farmRepository;
    private final RegionRepository regionRepository;
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final CacheService cacheService;
    private final FarmImageRepository farmImageRepository;

    // userRepository удалён — не нужен

    public FarmService(
        FarmRepository farmRepository,
        RegionRepository regionRepository,
        ActivityRepository activityRepository,
        BookingRepository bookingRepository,
        AccommodationRepository accommodationRepository,
        CacheService cacheService,
        FarmImageRepository farmImageRepository
    ) {
        this.farmRepository = farmRepository;
        this.regionRepository = regionRepository;
        this.activityRepository = activityRepository;
        this.bookingRepository = bookingRepository;
        this.accommodationRepository = accommodationRepository;
        this.cacheService = cacheService;
        this.farmImageRepository = farmImageRepository;
    }

    public List<FarmResponseDto> getAllFarms() {
        return farmRepository.findAll()
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    // Метод getFarmsByOwner удалён — больше не нужен

    public List<FarmResponseDto> getAllFarmsWithNPlusOne() {
        return farmRepository.findAllBy()
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public List<FarmResponseDto> getFarmsByRegion(String region) {
        return farmRepository.findByRegionName(region)
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public List<FarmResponseDto> getFarmsByRegionAndName(String region, String name) {
        List<Farm> farms = farmRepository.findByRegionNameContainingIgnoreCaseAndNameContainingIgnoreCase(region, name);
        return farms.stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public List<FarmResponseDto> getFarmsByName(String name) {
        return farmRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public FarmResponseDto getFarmById(Long id) {
        Farm farm = farmRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    FARM_NOT_FOUND + id
                )
            );
        return FarmMapper.toDto(farm);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDto> getFarmActivities(Long farmId) {
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    FARM_NOT_FOUND + farmId
                )
            );

        org.hibernate.Hibernate.initialize(farm.getActivities());

        return farm.getActivities()
            .stream()
            .map(ActivityMapper::toDto)
            .toList();
    }

    @Transactional
    public FarmResponseDto createFarm(FarmCreateDto dto) {
        if (farmRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Farm already exists: " + dto.getName()
            );
        }

        Farm farm = buildFarm(dto);
        // owner остаётся null — ферма без владельца

        FarmResponseDto result = FarmMapper.toDto(farmRepository.save(farm));
        cacheService.invalidateFarmSearchCache();

        return result;
    }

    @Transactional
    public FarmResponseDto createFarmWithAccommodations(FarmCreateDto dto) {
        if (farmRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Farm already exists: " + dto.getName()
            );
        }

        Farm farm = buildFarm(dto);

        Accommodation house = new Accommodation();
        house.setType(AccommodationType.HOUSE);
        house.setPrice(100);

        Accommodation tent = new Accommodation();
        tent.setType(AccommodationType.TENT);
        tent.setPrice(50);

        farm.addAccommodation(house);
        farm.addAccommodation(tent);

        FarmResponseDto result = FarmMapper.toDto(farmRepository.save(farm));
        cacheService.invalidateFarmSearchCache();

        return result;
    }

    @Transactional
    public FarmResponseDto updateFarm(Long id, FarmUpdateDto dto) {
        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                FARM_NOT_FOUND + id));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            farm.setName(dto.getName());
        }

        // Важно: обрабатываем boolean правильно
        farm.setActive(dto.isActive());

        if (dto.getRegion() != null && !dto.getRegion().isBlank()) {
            farm.setRegion(getOrCreateRegion(dto.getRegion()));
        }

        if (dto.getDescription() != null) {
            farm.setDescription(dto.getDescription());
        }

        if (dto.getEmail() != null) {
            farm.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            farm.setPhone(dto.getPhone());
        }

        if (dto.getEstablishedYear() != null) {
            farm.setEstablishedYear(dto.getEstablishedYear());
        }

        FarmResponseDto result = FarmMapper.toDto(farmRepository.save(farm));
        cacheService.invalidateFarmSearchCache();

        return result;
    }

    @Transactional
    public void deleteFarm(Long id) {
        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                FARM_NOT_FOUND + id));

        List<Accommodation> accommodations = accommodationRepository.findByFarmId(id);

        for (Accommodation accommodation : accommodations) {
            List<Booking> bookings = bookingRepository.findByAccommodationId(accommodation.getId());

            if (!bookings.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format(
                        "Cannot delete farm '%s' because it has %d active booking(s) for accommodation '%s'",
                        farm.getName(),
                        bookings.size(),
                        accommodation.getType().name()
                    )
                );
            }
        }

        // Удаляем связанные изображения
        List<FarmImage> images = farmImageRepository.findByFarmId(id);
        for (FarmImage image : images) {
            try {
                String fileName = Paths.get(image.getImageUrl()).getFileName().toString();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + e.getMessage());
            }
        }
        farmImageRepository.deleteByFarmId(id);

        farm.getActivities().clear();
        farm.getAccommodations().clear();

        farmRepository.delete(farm);
        cacheService.invalidateFarmSearchCache();
    }

    @Transactional
    public void addActivityToFarm(Long farmId, Long activityId) {
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    FARM_NOT_FOUND + farmId
                )
            );

        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Activity not found: " + activityId
                )
            );

        farm.getActivities().add(activity);
        activity.getFarms().add(farm);

        farmRepository.save(farm);
        cacheService.invalidateFarmSearchCache();
    }

    @Transactional(readOnly = true)
    public FarmEditDto getFarmForEdit(Long farmId) {
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, FARM_NOT_FOUND + farmId));

        FarmEditDto dto = new FarmEditDto();
        dto.setId(farm.getId());
        dto.setName(farm.getName());
        dto.setRegion(farm.getRegion().getName());
        dto.setActive(farm.isActive());
        dto.setDescription(farm.getDescription());
        dto.setEmail(farm.getEmail());
        dto.setPhone(farm.getPhone());
        dto.setEstablishedYear(farm.getEstablishedYear());

        dto.setActivities(farm.getActivities().stream()
            .map(a -> {
                FarmEditDto.ActivityDto ad = new FarmEditDto.ActivityDto();
                ad.setId(a.getId());
                ad.setName(a.getName());
                return ad;
            }).toList());

        dto.setAccommodations(farm.getAccommodations().stream()
            .map(a -> {
                FarmEditDto.AccommodationDto ad = new FarmEditDto.AccommodationDto();
                ad.setId(a.getId());
                ad.setType(a.getType());
                ad.setPrice(a.getPrice());
                return ad;
            }).toList());

        dto.setImageUrls(farmImageRepository.findByFarmId(farmId).stream()
            .map(FarmImage::getImageUrl)
            .toList());

        return dto;
    }

    @Transactional
    public void removeActivityFromFarm(Long farmId, Long activityId) {
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    FARM_NOT_FOUND + farmId
                )
            );

        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Activity not found: " + activityId
                )
            );

        farm.getActivities().remove(activity);
        activity.getFarms().remove(farm);

        farmRepository.save(farm);
        cacheService.invalidateFarmSearchCache();
    }

    @Transactional
    public void uploadImage(Long farmId, MultipartFile file, boolean isMain) {
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath.toFile());

            FarmImage farmImage = new FarmImage();
            farmImage.setFarm(farm);
            farmImage.setImageUrl("/uploads/farms/" + fileName);
            farmImage.setMain(isMain);

            if (isMain) {
                farmImageRepository.findByFarmId(farmId).forEach(img -> {
                    img.setMain(false);
                    farmImageRepository.save(img);
                });
            }

            farmImageRepository.save(farmImage);
            cacheService.invalidateFarmSearchCache();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image", e);
        }
    }

    @Transactional
    public void deleteImage(Long farmId, Long imageId) {
        FarmImage image = farmImageRepository.findById(imageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        if (!image.getFarm().getId().equals(farmId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image does not belong to this farm");
        }

        try {
            String fileName = Paths.get(image.getImageUrl()).getFileName().toString();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }

        farmImageRepository.delete(image);
        cacheService.invalidateFarmSearchCache();
    }

    @Transactional
    public void setMainImage(Long farmId, Long imageId) {
        FarmImage newMainImage = farmImageRepository.findById(imageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        if (!newMainImage.getFarm().getId().equals(farmId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image does not belong to this farm");
        }

        farmImageRepository.findByFarmId(farmId).forEach(img -> {
            img.setMain(false);
            farmImageRepository.save(img);
        });

        newMainImage.setMain(true);
        farmImageRepository.save(newMainImage);
        cacheService.invalidateFarmSearchCache();
    }

    public List<FarmResponseDto> findActiveFarmsByAccommodationTypes(Set<String> accommodationTypes) {
        FarmSearchCriteria criteria = FarmSearchCriteria.builder()
            .active(true)
            .accommodationTypes(accommodationTypes)
            .build();

        List<FarmResponseDto> cached = cacheService.getCachedFarmSearch(criteria);

        if (cached != null) {
            return cached;
        }

        List<String> typesList = accommodationTypes != null ? List.copyOf(accommodationTypes) : List.of();

        List<Farm> farms = farmRepository.findActiveFarmsWithAccommodationTypesEager(typesList);

        List<FarmResponseDto> result = farms.stream()
            .map(FarmMapper::toDto)
            .toList();

        cacheService.putFarmSearch(criteria, result);

        return result;
    }

    public List<FarmResponseDto> findActiveFarmsByNameNative(String namePart) {
        List<Farm> farms = farmRepository.findActiveFarmsByNameNative(namePart);
        return farms.stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public Page<FarmResponseDto> getAllFarmsPaginated(Pageable pageable) {
        Pageable defaultPageable = pageable.getSort().isSorted()
            ? pageable
            : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name"));

        Page<Long> idsPage = farmRepository.findAllIds(defaultPageable);

        if (idsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Farm> farms = farmRepository.findAllByIdIn(idsPage.getContent());

        Map<Long, FarmResponseDto> farmMap = farms.stream()
            .map(FarmMapper::toDto)
            .collect(Collectors.toMap(FarmResponseDto::id, Function.identity()));

        List<FarmResponseDto> sortedDtos = idsPage.getContent()
            .stream()
            .map(farmMap::get)
            .filter(Objects::nonNull)
            .toList();

        return new PageImpl<>(sortedDtos, defaultPageable, idsPage.getTotalElements());
    }

    public Page<FarmResponseDto> findActiveFarmsByAccommodationTypesPaginated(
        Set<String> accommodationTypes, Pageable pageable) {

        List<String> typesList = accommodationTypes != null ? List.copyOf(accommodationTypes) : List.of();

        Page<Farm> farmPage = farmRepository.findActiveFarmsWithAccommodationTypesPaginated(typesList, pageable);

        return farmPage.map(FarmMapper::toDto);
    }

    public Page<FarmResponseDto> findActiveFarmsByAccommodationTypesNativePaginated(
        Set<String> accommodationTypes, Pageable pageable) {

        Pageable withoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        List<String> typesList = accommodationTypes != null ? List.copyOf(accommodationTypes) : List.of();

        Page<Farm> farmPage = farmRepository.findActiveFarmsWithAccommodationTypesNativePaginated(typesList, withoutSort);

        if (farmPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> farmIds = farmPage.getContent().stream()
            .map(Farm::getId)
            .toList();

        List<Farm> farmsWithData = farmRepository.findAllByIdIn(farmIds);

        Map<Long, Farm> farmMap = farmsWithData.stream()
            .collect(Collectors.toMap(Farm::getId, Function.identity()));

        List<FarmResponseDto> sortedResult = farmIds.stream()
            .map(farmMap::get)
            .filter(Objects::nonNull)
            .map(FarmMapper::toDto)
            .toList();

        return new PageImpl<>(sortedResult, withoutSort, farmPage.getTotalElements());
    }

    public int getCacheSize() {
        return cacheService.getCacheSize();
    }

    private Farm buildFarm(FarmCreateDto dto) {
        if (farmRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Farm already exists: " + dto.getName()
            );
        }

        Farm farm = new Farm();
        farm.setName(dto.getName());
        farm.setActive(dto.isActive());
        farm.setRegion(getOrCreateRegion(dto.getRegion()));
        farm.setDescription(dto.getDescription());
        farm.setEmail(dto.getEmail());
        farm.setPhone(dto.getPhone());
        farm.setEstablishedYear(dto.getEstablishedYear());

        return farm;
    }

    private Region getOrCreateRegion(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                REGION_REQUIRED_MESSAGE
            );
        }

        return regionRepository.findByName(name)
            .orElseGet(() -> {
                Region region = new Region();
                region.setName(name);
                return regionRepository.save(region);
            });
    }
}