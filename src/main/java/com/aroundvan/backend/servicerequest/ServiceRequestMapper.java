package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.servicerequest.dto.ServiceRequestResponse;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestMapper {

    public ServiceRequestResponse toResponse(
            ServiceRequest serviceRequest,
            Double distanceKm,
            boolean seen
    ) {
        Location location = serviceRequest.getLocation();

        return new ServiceRequestResponse(
                serviceRequest.getId(),
                serviceRequest.getRequestType(),
                serviceRequest.getCategory().name(),
                serviceRequest.getImportance().name(),
                serviceRequest.getStatus().name(),
                serviceRequest.getAddress(),
                location != null && location.getNeighbourhood() != null
                        ? location.getNeighbourhood().getName()
                        : null,
                serviceRequest.getLocalArea(),
                location != null ? location.getLatitude() : null,
                location != null ? location.getLongitude() : null,
                distanceKm == null ? null : Math.round(distanceKm * 10.0) / 10.0,
                serviceRequest.getOpenedAt(),
                seen
        );
    }
}
