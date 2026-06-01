package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.FarmImage;
import com.example.greenalpinepeaks.dto.ImageResponseDto;
import com.example.greenalpinepeaks.repository.FarmImageRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
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
import java.util.UUID;

@Service
public class FarmImageService {

    private final FarmImageRepository farmImageRepository;
    private final FarmRepository farmRepository;

    private static final String UPLOAD_DIR =
        System.getProperty("user.dir") + "/uploads/";

    public FarmImageService(
        FarmImageRepository farmImageRepository,
        FarmRepository farmRepository
    ) {
        this.farmImageRepository = farmImageRepository;
        this.farmRepository = farmRepository;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }
    }

    @Transactional
    public void uploadImage(Long farmId, MultipartFile file, boolean isMain) {
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Farm not found"
                ));

        // Проверка прав УДАЛЕНА - админ может загружать фото для любой фермы

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Image file is empty"
            );
        }

        // Если загружаем главное фото, снимаем флаг main со всех остальных
        if (isMain) {
            List<FarmImage> existingImages = farmImageRepository.findByFarmId(farmId);
            for (FarmImage image : existingImages) {
                if (image.isMain()) {
                    image.setMain(false);
                    farmImageRepository.save(image);
                }
            }
        }

        try {
            String originalFilename = file.getOriginalFilename();

            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file name"
                );
            }

            String extension =
                originalFilename.substring(originalFilename.lastIndexOf('.'));

            String fileName = UUID.randomUUID() + extension;

            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            Files.write(filePath, file.getBytes());

            FarmImage farmImage = new FarmImage();
            farmImage.setImageUrl("/uploads/" + fileName);
            farmImage.setMain(isMain);
            farmImage.setFarm(farm);

            farmImageRepository.save(farmImage);

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to upload image: " + e.getMessage()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ImageResponseDto> getFarmImages(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Farm not found"
            );
        }

        return farmImageRepository.findByFarmId(farmId)
            .stream()
            .map(img ->
                new ImageResponseDto(
                    img.getId(),
                    img.getImageUrl(),
                    img.isMain()
                ))
            .toList();
    }

    @Transactional
    public void deleteImage(Long farmId, Long imageId) {
        FarmImage image = farmImageRepository.findById(imageId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Image not found"
                ));

        // Проверяем принадлежность изображения ферме
        if (!image.getFarm().getId().equals(farmId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Image not found for this farm"
            );
        }

        // Удаляем файл с диска
        try {
            String fileName = Paths.get(image.getImageUrl()).getFileName().toString();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                System.err.println("File not found on disk: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file from disk: " + e.getMessage());
        }

        // Удаляем запись из БД
        farmImageRepository.delete(image);

        // Если удалили главное фото, делаем первое из оставшихся главным
        List<FarmImage> remainingImages = farmImageRepository.findByFarmId(farmId);
        if (!remainingImages.isEmpty() && image.isMain()) {
            FarmImage firstImage = remainingImages.get(0);
            firstImage.setMain(true);
            farmImageRepository.save(firstImage);
        }
    }

    @Transactional
    public void setMainImage(Long farmId, Long imageId) {
        // Находим изображение
        FarmImage newMainImage = farmImageRepository.findById(imageId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Image not found"
            ));

        // Проверяем, что изображение принадлежит ферме
        if (!newMainImage.getFarm().getId().equals(farmId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Image does not belong to this farm"
            );
        }

        // Снимаем флаг main со всех изображений фермы
        List<FarmImage> farmImages = farmImageRepository.findByFarmId(farmId);
        for (FarmImage image : farmImages) {
            if (image.isMain()) {
                image.setMain(false);
                farmImageRepository.save(image);
            }
        }

        // Устанавливаем новое главное изображение
        newMainImage.setMain(true);
        farmImageRepository.save(newMainImage);
    }
}