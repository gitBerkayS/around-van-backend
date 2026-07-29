package com.aroundvan.backend.user.dto;

import com.aroundvan.backend.servicerequest.ServiceRequestCategory;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateServiceRequestPreferencesRequest(
        @NotNull Set<ServiceRequestCategory> categories
) {
}
