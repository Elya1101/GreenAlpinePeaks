package com.example.greenalpinepeaks.dto;

public class FarmCreateDto {

    private String name;
    private boolean active;
    private String region;
    private String description;
    private String email;
    private String phone;
    private Integer establishedYear;

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getRegion() {
        return region;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getEstablishedYear() {
        return establishedYear;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEstablishedYear(Integer establishedYear) {
        this.establishedYear = establishedYear;
    }
}