package com.aroundvan.backend.environment.aqhi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AqhiRegion {

    @Id
    private String locationId;

    @Column(nullable = false)
    private String name;

    private Double latitude;
    private Double longitude;

}
