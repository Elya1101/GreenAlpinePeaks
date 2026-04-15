package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheService {

    private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

    private final Map<FarmSearchCriteria, List<FarmResponseDto>> farmSearchCache = new HashMap<>();

    public List<FarmResponseDto> getCachedFarmSearch(FarmSearchCriteria criteria) {
        List<FarmResponseDto> cached = farmSearchCache.get(criteria);
        if (cached != null) {
            LOG.info("Cache HIT for criteria: {}", criteria);
        } else {
            LOG.info("Cache MISS for criteria: {}", criteria);
        }
        return cached;
    }

    public void putFarmSearch(FarmSearchCriteria criteria, List<FarmResponseDto> results) {
        LOG.info("Caching result for criteria: {}", criteria);
        farmSearchCache.put(criteria, results);
    }

    public void invalidateFarmSearchCache() {
        LOG.info("Invalidating farm search cache. Size before: {}", farmSearchCache.size());
        farmSearchCache.clear();
    }

    public int getCacheSize() {
        return farmSearchCache.size();
    }
}