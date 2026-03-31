package com.example.greenalpinepeaks.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "activities", fetch = FetchType.LAZY)
    private List<Farm> farms;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Farm> getFarms() {
        return farms;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFarms(List<Farm> farms) {
        this.farms = farms;
    }
}