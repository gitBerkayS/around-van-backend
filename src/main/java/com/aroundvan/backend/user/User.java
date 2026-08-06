package com.aroundvan.backend.user;

import com.aroundvan.backend.gas.FuelType;
import com.aroundvan.backend.location.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Getter
@Setter
@Table(name= "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    @OneToOne(fetch = FetchType.LAZY)
    private Location homeLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_fuel_type", nullable = false, length = 20)
    private FuelType preferredFuelType = FuelType.REGULAR;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }



}
