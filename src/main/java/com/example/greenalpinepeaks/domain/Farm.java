package com.example.greenalpinepeaks.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "farms")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToMany(mappedBy = "farm",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true)
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public Region getRegion() {
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

    public Set<Accommodation> getAccommodations() {
        return accommodations;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setAccommodations(Set<Accommodation> accommodations) {
        this.accommodations = accommodations;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
}