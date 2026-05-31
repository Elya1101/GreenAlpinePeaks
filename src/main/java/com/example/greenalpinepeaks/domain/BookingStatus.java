package com.example.greenalpinepeaks.domain;

public enum BookingStatus {
    PENDING("⏳ В ожидании"),
    APPROVED("✅ Одобрено"),
    REJECTED("❌ Отклонено");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}