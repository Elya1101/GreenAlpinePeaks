package com.example.greenalpinepeaks.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для ответа с информацией об изображении фермы")
public class ImageResponseDto {

    @Schema(description = "Уникальный идентификатор изображения", example = "1")
    private Long id;

    @Schema(description = "URL изображения", example = "/uploads/abc123.jpg")
    private String imageUrl;

    @Schema(description = "Является ли изображение главным", example = "false")
    private boolean isMain;

    // Конструкторы
    public ImageResponseDto() {

    }

    public ImageResponseDto(Long id, String imageUrl, boolean isMain) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isMain = isMain;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }
}