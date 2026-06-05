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

    // Связь со справочником типов жилья (НОВЫЙ ПОДХОД)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private AccommodationType type;

    // Цена за неделю
    private double price;

    // Связь с фермой
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    // Связь с бронированиями - ИСПРАВЛЕНО: инициализируем пустым списком
    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();  // ← ВАЖНО: инициализируем!

    // Конструктор для нового подхода (со справочником)
    public Accommodation(AccommodationType type, double price, Farm farm) {
        this.type = type;
        this.price = price;
        this.farm = farm;
        this.bookings = new ArrayList<>();  // ← также инициализируем в конструкторе
    }

    // Удобный метод для получения названия типа жилья
    public String getTypeName() {
        return type != null ? type.getName() : null;
    }

    // Удобный метод для получения кода типа жилья
    public String getTypeCode() {
        return type != null ? type.getCode() : null;
    }

    // Вспомогательный метод для добавления бронирования
    public void addBooking(Booking booking) {
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        this.bookings.add(booking);
        booking.setAccommodation(this);
    }

    // Вспомогательный метод для удаления бронирования
    public void removeBooking(Booking booking) {
        if (this.bookings != null) {
            this.bookings.remove(booking);
            booking.setAccommodation(null);
        }
    }
}