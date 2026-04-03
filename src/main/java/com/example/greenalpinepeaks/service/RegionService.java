package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.repository.RegionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<Region> getAll() {
        return regionRepository.findAll();
    }

    public Region create(Region region) {
        return regionRepository.save(region);
    }

    public Region getById(Long id) {
        return regionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Region update(Long id, Region updated) {
        Region region = getById(id);
        region.setName(updated.getName());
        return regionRepository.save(region);
    }

    public void delete(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        regionRepository.deleteById(id);
    }
}