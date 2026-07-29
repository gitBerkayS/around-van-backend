package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_service_request_preference")
@IdClass(UserServiceRequestPreferenceId.class)
public class UserServiceRequestPreference {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ServiceRequestCategory category;
}
