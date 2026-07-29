package com.aroundvan.backend.servicerequest;

import java.io.Serializable;
import java.util.Objects;

public class UserServiceRequestSeenId implements Serializable {

    private Long user;
    private Long serviceRequest;

    public UserServiceRequestSeenId() {
    }

    public UserServiceRequestSeenId(Long user, Long serviceRequest) {
        this.user = user;
        this.serviceRequest = serviceRequest;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserServiceRequestSeenId that)) {
            return false;
        }
        return Objects.equals(user, that.user)
                && Objects.equals(serviceRequest, that.serviceRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, serviceRequest);
    }
}
