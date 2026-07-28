package com.aroundvan.backend.location;

import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighbourhood_id")
    private Neighbourhood neighbourhood;
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private double longitude;

    private String postalCodePrefix;

    public Location(Neighbourhood neighbourhood, Double latitude, Double longitude) {
        this.neighbourhood = neighbourhood;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
