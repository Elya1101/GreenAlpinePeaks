package com.example.greenalpinepeaks.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    @Column(length = 20)
    private String phone;

    private Integer establishedYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToMany(
        mappedBy = "farm",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<Accommodation> accommodations = new HashSet<>();

    public void addAccommodation(Accommodation acc) {
        accommodations.add(acc);
        acc.setFarm(this);
    }

    public void removeAccommodation(Accommodation acc) {
        accommodations.remove(acc);
        acc.setFarm(null);
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "farm_activity",
        joinColumns = @JoinColumn(name = "farm_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activities = new HashSet<>();
}