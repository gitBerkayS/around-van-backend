package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import com.aroundvan.backend.servicerequest.dto.ServiceRequestResponse;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private static final int DEFAULT_NEAR_LIMIT = 50;
    private static final int MAX_NEAR_LIMIT = 100;

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserServiceRequestSeenRepository seenRepository;
    private final UserServiceRequestPreferenceRepository preferenceRepository;
    private final ServiceRequestMapper serviceRequestMapper;
    private final LocationService locationService;

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getImportantServiceRequestsNearbyForUser(User user) {
        requireHomeLocation(user);

        Neighbourhood neighbourhood = user.getHomeLocation().getNeighbourhood();
        if (neighbourhood == null) {
            return List.of();
        }

        Set<ServiceRequestCategory> categories = enabledCategories(user);
        if (categories.isEmpty()) {
            return List.of();
        }

        List<ServiceRequest> requests = serviceRequestRepository.findOpenImportantInNeighbourhood(
                ServiceRequestStatus.OPEN,
                ServiceRequestImportance.IMPORTANT,
                categories,
                neighbourhood
        );

        Set<Long> seenIds = seenIdsFor(user, requests);

        return requests.stream()
                .filter(request -> !seenIds.contains(request.getId()))
                .filter(request -> request.getLocation() != null)
                .map(request -> serviceRequestMapper.toResponse(
                        request,
                        locationService.calculateDistanceFromUserInKm(user, request.getLocation()),
                        false
                ))
                .sorted(Comparator.comparing(
                        ServiceRequestResponse::distanceKm,
                        Comparator.nullsLast(Double::compareTo)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getServiceRequestsNearbyForUser(
            User user,
            ServiceRequestImportance importance,
            Integer limit
    ) {
        requireHomeLocation(user);

        Set<ServiceRequestCategory> categories = enabledCategories(user);
        if (categories.isEmpty()) {
            return List.of();
        }

        List<ServiceRequest> requests = serviceRequestRepository.findOpenVisibleWithLocation(
                ServiceRequestStatus.OPEN,
                ServiceRequestImportance.HIDDEN,
                categories
        );

        Set<Long> seenIds = seenIdsFor(user, requests);
        int resolvedLimit = resolveLimit(limit);

        return requests.stream()
                .filter(request -> importance == null || request.getImportance() == importance)
                .filter(request -> request.getImportance() != ServiceRequestImportance.HIDDEN)
                .map(request -> {
                    double distance = locationService.calculateDistanceFromUserInKm(
                            user,
                            request.getLocation()
                    );
                    boolean seen = seenIds.contains(request.getId());
                    return serviceRequestMapper.toResponse(request, distance, seen);
                })
                .sorted(Comparator.comparingDouble(ServiceRequestResponse::distanceKm))
                .limit(resolvedLimit)
                .toList();
    }

    @Transactional
    public void markAsSeen(User user, Long serviceRequestId) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Service request not found"
                ));

        if (seenRepository.existsByUserAndServiceRequest(user, serviceRequest)) {
            return;
        }

        UserServiceRequestSeen seen = new UserServiceRequestSeen();
        seen.setUser(user);
        seen.setServiceRequest(serviceRequest);
        seen.setSeenAt(Instant.now());
        seenRepository.save(seen);
    }

    @Transactional(readOnly = true)
    public Set<ServiceRequestCategory> getPreferences(User user) {
        return enabledCategories(user);
    }

    @Transactional
    public Set<ServiceRequestCategory> updatePreferences(
            User user,
            Set<ServiceRequestCategory> categories
    ) {
        preferenceRepository.deleteAllByUser(user);
        preferenceRepository.flush();

        Set<ServiceRequestCategory> enabled;
        if (categories == null || categories.isEmpty()) {
            enabled = EnumSet.noneOf(ServiceRequestCategory.class);
        } else {
            enabled = EnumSet.copyOf(categories);
        }

        for (ServiceRequestCategory category : enabled) {
            UserServiceRequestPreference preference = new UserServiceRequestPreference();
            preference.setUser(user);
            preference.setCategory(category);
            preferenceRepository.save(preference);
        }

        return enabled;
    }

    private Set<ServiceRequestCategory> enabledCategories(User user) {
        List<UserServiceRequestPreference> preferences = preferenceRepository.findAllByUser(user);

        if (preferences.isEmpty()) {
            return EnumSet.allOf(ServiceRequestCategory.class);
        }

        return preferences.stream()
                .map(UserServiceRequestPreference::getCategory)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ServiceRequestCategory.class)));
    }

    private Set<Long> seenIdsFor(User user, List<ServiceRequest> requests) {
        if (requests.isEmpty()) {
            return Set.of();
        }

        List<Long> ids = requests.stream().map(ServiceRequest::getId).toList();
        return seenRepository.findSeenServiceRequestIds(user, ids);
    }

    private void requireHomeLocation(User user) {
        Location homeLocation = user.getHomeLocation();
        if (homeLocation == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Set your home location before requesting nearby service requests"
            );
        }
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_NEAR_LIMIT;
        }
        return Math.min(limit, MAX_NEAR_LIMIT);
    }
}
