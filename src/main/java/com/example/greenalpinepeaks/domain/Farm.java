package com.example.greenalpinepeaks.domain;  // Пакет проекта

// Импорт аннотаций JPA

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity  // Говорим Spring, что это сущность
@Table(name = "farms")  // Имя таблицы в базе данных
public class Farm {

    @Id  // Это поле будет уникальным идентификатором
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Автоматическая генерация ID
    private Long id;  // Идентификатор фермы

    @Column(nullable = false)  // Указываем, что это обязательное поле
    private String name;  // Название фермы

    private String region;  // Регион фермы (необязательное поле)

    private boolean active;  // Статус активности фермы (неактивная/активная)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}