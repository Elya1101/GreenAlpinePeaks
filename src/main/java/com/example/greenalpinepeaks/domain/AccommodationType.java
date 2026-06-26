package com.example.greenalpinepeaks.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;  // ➕ добавить
import java.util.List;

@Entity
@Table(name = "accommodation_types")
@Getter
@Setter
@NoArgsConstructor
public class AccommodationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private List<Accommodation> accommodations = new ArrayList<>();

    public AccommodationType(String name, String code) {
        this.name = name;
        this.code = code;
    }
}