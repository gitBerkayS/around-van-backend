package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.servicerequest.vancouver.Vancouver311Client;
import com.aroundvan.backend.servicerequest.vancouver.dto.Vancouver311RecordsResponse.Vancouver311Record;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ServiceRequestImportService {

    private final Vancouver311Client vancouver311Client;
    private final ServiceRequestTypeRules typeRules;
    private final ServiceRequestRepository serviceRequestRepository;
    private final LocationService locationService;

    @Transactional
    public int importOpenServiceRequests() {
        List<Vancouver311Record> records = vancouver311Client.fetchOpenAllowlistedRecords();

        Instant syncedAt = Instant.now();
        Set<String> importedKeys = new HashSet<>();
        List<ServiceRequest> toSave = new ArrayList<>();

        for (Vancouver311Record record : records) {
            Optional<ServiceRequestTypeRules.Classification> classification =
                    typeRules.classify(record.serviceRequestType());

            if (classification.isEmpty()) {
                continue;
            }

            if (record.address() == null || record.address().isBlank()
                    || record.latitude() == null || record.longitude() == null
                    || record.serviceRequestOpenTimestamp() == null
                    || record.serviceRequestType() == null) {
                continue;
            }

            String externalKey = externalKey(
                    record.serviceRequestType(),
                    record.address(),
                    record.serviceRequestOpenTimestamp()
            );

            if (!importedKeys.add(externalKey)) {
                continue;
            }

            ServiceRequest serviceRequest = serviceRequestRepository
                    .findByExternalKey(externalKey)
                    .orElseGet(ServiceRequest::new);

            applyRecord(serviceRequest, record, classification.get(), externalKey, syncedAt);

            Location location = locationService.resolveServiceRequestLocation(
                    serviceRequest.getLocation(),
                    record.latitude(),
                    record.longitude()
            );
            serviceRequest.setLocation(location);

            toSave.add(serviceRequest);
        }

        serviceRequestRepository.saveAll(toSave);
        closeMissingOpenRequests(importedKeys, syncedAt);

        return toSave.size();
    }

    private void applyRecord(
            ServiceRequest serviceRequest,
            Vancouver311Record record,
            ServiceRequestTypeRules.Classification classification,
            String externalKey,
            Instant syncedAt
    ) {
        serviceRequest.setExternalKey(externalKey);
        serviceRequest.setRequestType(record.serviceRequestType());
        serviceRequest.setCategory(classification.category());
        serviceRequest.setImportance(classification.importance());
        serviceRequest.setStatus(ServiceRequestStatus.OPEN);
        serviceRequest.setDepartment(record.department());
        serviceRequest.setAddress(record.address().trim());
        serviceRequest.setLocalArea(record.localArea());
        serviceRequest.setChannel(record.channel());
        serviceRequest.setOpenedAt(record.serviceRequestOpenTimestamp());
        serviceRequest.setLastModifiedAt(record.lastModifiedTimestamp());
        serviceRequest.setClosedAt(null);
        serviceRequest.setLastSyncedAt(syncedAt);
    }

    private void closeMissingOpenRequests(Set<String> importedKeys, Instant syncedAt) {
        List<ServiceRequest> closed = serviceRequestRepository
                .findAllByStatus(ServiceRequestStatus.OPEN)
                .stream()
                .filter(request -> !importedKeys.contains(request.getExternalKey()))
                .peek(request -> {
                    request.setStatus(ServiceRequestStatus.CLOSED);
                    request.setClosedAt(syncedAt);
                    request.setLastSyncedAt(syncedAt);
                })
                .toList();

        if (!closed.isEmpty()) {
            serviceRequestRepository.saveAll(closed);
        }
    }

    static String externalKey(String requestType, String address, Instant openedAt) {
        String raw = requestType + "|" + address.trim().toUpperCase() + "|" + openedAt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 not available", exception);
        }
    }
}
