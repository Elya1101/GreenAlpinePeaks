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

    private static final Logger log = LoggerFactory.getLogger(GreenAlpinePeaksApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GreenAlpinePeaksApplication.class, args);

        // Получаем репозиторий
        FarmRepository farmRepository = context.getBean(FarmRepository.class);

        // Создаём новую ферму
        Farm farm = new Farm();
        farm.setName("Альпийская ферма");
        farm.setRegion("Долина Лугано");
        farm.setActive(true);

        // Сохраняем в базу
        farmRepository.save(farm);

        log.info("Новая ферма сохранена: {} - {}", farm.getName(), farm.getRegion());

        // Проверяем: выводим все фермы
        log.info("Все фермы в базе:");
        farmRepository.findAll().forEach(f ->
            log.info("{} - {}", f.getName(), f.getRegion())
        );
    }
}