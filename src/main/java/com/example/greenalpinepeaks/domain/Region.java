package com.example.greenalpinepeaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "regions")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    @JsonIgnore
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