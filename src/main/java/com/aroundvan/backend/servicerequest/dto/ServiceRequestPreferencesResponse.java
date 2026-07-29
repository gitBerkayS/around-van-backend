package com.aroundvan.backend.servicerequest.dto;

import com.aroundvan.backend.servicerequest.ServiceRequestCategory;

import java.util.Set;

public record ServiceRequestPreferencesResponse(
        Set<ServiceRequestCategory> categories
) {
}
