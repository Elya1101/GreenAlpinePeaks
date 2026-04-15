package com.example.greenalpinepeaks.dto;

import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public class FarmSearchCriteria {

    private final String regionName;
    private final Boolean active;
    private final Set<String> accommodationTypes;
    private final String nameContains;

    private FarmSearchCriteria(String regionName, Boolean active, Set<String> accommodationTypes, String nameContains) {
        this.regionName = regionName;
        this.active = active;
        this.accommodationTypes = accommodationTypes != null ? Set.copyOf(accommodationTypes) : null;
        this.nameContains = nameContains;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRegionName() {
        return regionName;
    }

    public Boolean getActive() {
        return active;
    }

    public Set<String> getAccommodationTypes() {
        return accommodationTypes;
    }

    public String getNameContains() {
        return nameContains;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FarmSearchCriteria that = (FarmSearchCriteria) o;
        return Objects.equals(regionName, that.regionName) &&
            Objects.equals(active, that.active) &&
            Objects.equals(accommodationTypes, that.accommodationTypes) &&
            Objects.equals(nameContains, that.nameContains);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionName, active, accommodationTypes, nameContains);
    }

    @Override
    public String toString() {
        return "FarmSearchCriteria{" +
            "regionName='" + regionName + '\'' +
            ", active=" + active +
            ", accommodationTypes=" + accommodationTypes +
            ", nameContains='" + nameContains + '\'' +
            '}';
    }

    public static class Builder {
        private String regionName;
        private Boolean active;
        private Set<String> accommodationTypes;
        private String nameContains;

        public Builder regionName(String regionName) {
            this.regionName = regionName;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder accommodationTypes(Set<String> accommodationTypes) {
            this.accommodationTypes = accommodationTypes;
            return this;
        }

        public Builder nameContains(String nameContains) {
            this.nameContains = nameContains;
            return this;
        }

        public FarmSearchCriteria build() {
            return new FarmSearchCriteria(regionName, active, accommodationTypes, nameContains);
        }
    }
}