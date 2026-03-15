package com.example.greenalpinepeaks;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.repository.FarmRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class GreenAlpinePeaksApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GreenAlpinePeaksApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GreenAlpinePeaksApplication.class, args);

        FarmRepository farmRepository = context.getBean(FarmRepository.class);

        Farm farm = new Farm();
        farm.setName("Альпийская ферма");
        farm.setRegion("Долина Лугано");
        farm.setActive(true);

        farmRepository.save(farm);

        LOG.info("Новая ферма сохранена: {} - {}", farm.getName(), farm.getRegion());

        LOG.info("Все фермы в базе:");
        farmRepository.findAll().forEach(f ->
            LOG.info("{} - {}", f.getName(), f.getRegion())
        );
    }
}