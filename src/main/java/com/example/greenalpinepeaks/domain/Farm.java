package com.example.greenalpinepeaks.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "farms")
@Getter
@Setter
@NoArgsConstructor
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private boolean active;
    private String description;
    private String email;
    private String phone;
    private Integer establishedYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToMany(
        mappedBy = "farm",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private Set<Accommodation> accommodations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "farm_activity",
        joinColumns = @JoinColumn(name = "farm_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activities;

    @OneToMany(mappedBy = "farm", fetch = FetchType.LAZY)
    private Set<Booking> bookings;
}