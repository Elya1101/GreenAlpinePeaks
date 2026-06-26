package com.example.greenalpinepeaks.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accommodations")
@Getter
@Setter
@NoArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private AccommodationType type;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    public Accommodation(AccommodationType type, double price, Farm farm) {
        this.type = type;
        this.price = price;
        this.farm = farm;
        this.bookings = new ArrayList<>();
    }

    public String getTypeName() {
        return type != null ? type.getName() : null;
    }

    public String getTypeCode() {
        return type != null ? type.getCode() : null;
    }

    public void addBooking(Booking booking) {
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        this.bookings.add(booking);
        booking.setAccommodation(this);
    }

    public void removeBooking(Booking booking) {
        if (this.bookings != null) {
            this.bookings.remove(booking);
            booking.setAccommodation(null);
        }
    }
}