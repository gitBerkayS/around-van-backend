package com.aroundvan.backend.api.servicerequest;

import com.aroundvan.backend.servicerequest.ServiceRequestImportance;
import com.aroundvan.backend.servicerequest.ServiceRequestService;
import com.aroundvan.backend.servicerequest.dto.ServiceRequestResponse;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final UserService userService;

    @GetMapping("/important/near")
    public List<ServiceRequestResponse> getImportantNearby() {
        User user = requireUserWithHomeLocation();
        return serviceRequestService.getImportantServiceRequestsNearbyForUser(user);
    }

    @GetMapping("/near")
    public List<ServiceRequestResponse> getNearby(
            @RequestParam(required = false) String importance,
            @RequestParam(required = false) Integer limit
    ) {
        User user = requireUserWithHomeLocation();
        ServiceRequestImportance resolvedImportance = parseImportance(importance);
        return serviceRequestService.getServiceRequestsNearbyForUser(user, resolvedImportance, limit);
    }

    @PostMapping("/{id}/seen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsSeen(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        serviceRequestService.markAsSeen(user, id);
    }

    private User requireUserWithHomeLocation() {
        User user = userService.getCurrentUser();

        if (user.getHomeLocation() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Set your home location before requesting nearby service requests"
            );
        }

        return user;
    }

    private static ServiceRequestImportance parseImportance(String importance) {
        if (importance == null || importance.isBlank()) {
            return null;
        }

        try {
            ServiceRequestImportance parsed = ServiceRequestImportance.valueOf(importance.trim().toUpperCase());
            if (parsed == ServiceRequestImportance.HIDDEN) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot filter by HIDDEN importance"
                );
            }
            return parsed;
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid importance. Use IMPORTANT or LOW"
            );
        }
    }
}
