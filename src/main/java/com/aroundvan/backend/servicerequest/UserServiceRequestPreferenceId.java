package com.aroundvan.backend.servicerequest;

import java.io.Serializable;
import java.util.Objects;

public class UserServiceRequestPreferenceId implements Serializable {

    private Long user;
    private ServiceRequestCategory category;

    public UserServiceRequestPreferenceId() {
    }

    public UserServiceRequestPreferenceId(Long user, ServiceRequestCategory category) {
        this.user = user;
        this.category = category;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserServiceRequestPreferenceId that)) {
            return false;
        }
        return Objects.equals(user, that.user)
                && category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, category);
    }
}
