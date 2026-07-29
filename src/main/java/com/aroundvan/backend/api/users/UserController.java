package com.aroundvan.backend.api.users;

import com.aroundvan.backend.servicerequest.ServiceRequestCategory;
import com.aroundvan.backend.servicerequest.ServiceRequestService;
import com.aroundvan.backend.servicerequest.dto.ServiceRequestPreferencesResponse;
import com.aroundvan.backend.user.UserDTO;
import com.aroundvan.backend.user.UserService;
import com.aroundvan.backend.user.dto.UpdateFuelPreferenceRequest;
import com.aroundvan.backend.user.dto.UpdateLocationRequest;
import com.aroundvan.backend.user.dto.UpdateServiceRequestPreferencesRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ServiceRequestService serviceRequestService;

    @GetMapping("/me")
    public UserDTO getCurrentUser() {
        return userService.toDto(userService.getCurrentUser());
    }

    @PutMapping("/me/location")
    public UserDTO updateHomeLocation(
            @Valid @RequestBody UpdateLocationRequest request) {
        return userService.updateHomeLocation(request);
    }

    @PutMapping("/me/fuel-preference")
    public UserDTO updateFuelPreference(
            @Valid @RequestBody UpdateFuelPreferenceRequest request) {
        return userService.updateFuelPreference(request);
    }

    @GetMapping("/me/service-request-preferences")
    public ServiceRequestPreferencesResponse getServiceRequestPreferences() {
        Set<ServiceRequestCategory> categories =
                serviceRequestService.getPreferences(userService.getCurrentUser());
        return new ServiceRequestPreferencesResponse(categories);
    }

    @PutMapping("/me/service-request-preferences")
    public ServiceRequestPreferencesResponse updateServiceRequestPreferences(
            @Valid @RequestBody UpdateServiceRequestPreferencesRequest request
    ) {
        Set<ServiceRequestCategory> categories = serviceRequestService.updatePreferences(
                userService.getCurrentUser(),
                request.categories()
        );
        return new ServiceRequestPreferencesResponse(categories);
    }
}
