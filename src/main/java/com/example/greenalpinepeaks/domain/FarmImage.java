package com.example.greenalpinepeaks.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farm_images")
@Getter
@Setter
@NoArgsConstructor
public class FarmImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "is_main", nullable = false)
    private boolean isMain = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;
}