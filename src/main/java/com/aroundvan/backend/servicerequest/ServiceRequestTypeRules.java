package com.aroundvan.backend.servicerequest;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ServiceRequestTypeRules {

    public record Classification(
            ServiceRequestCategory category,
            ServiceRequestImportance importance
    ) {
    }

    private final Map<String, Classification> rules;

    public ServiceRequestTypeRules() {
        Map<String, Classification> mapped = new LinkedHashMap<>();

        //ROAD — IMPORTANT
        put(mapped, "Pothole Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Street Repair Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Sidewalk Repair Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Street Surface Water Flooding Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Traffic Signal Repair Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Street Light Out Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Street or Traffic Light Utility Damage Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.IMPORTANT);

        //ROAD — LOW
        put(mapped, "General Street Issues Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.LOW);
        put(mapped, "Street Cleaning and Debris Pick Up Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.LOW);
        put(mapped, "Traffic Calming Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.LOW);
        put(mapped, "New Crosswalk Marking Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.LOW);
        put(mapped, "Street Construction Concern Case", ServiceRequestCategory.ROAD, ServiceRequestImportance.LOW);

        //WATER — IMPORTANT
        put(mapped, "Water Leak Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Sewer Backup Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Damage to Water System Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Water Quality Concern Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Water Pressure Concern Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Sewer Maintenance Hole Concern Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Catch Basin Concern Case", ServiceRequestCategory.WATER, ServiceRequestImportance.IMPORTANT);

        //WATER — LOW
        put(mapped, "Water Hydrant Concern Case", ServiceRequestCategory.WATER, ServiceRequestImportance.LOW);
        put(mapped, "High Water Consumption Concern Case", ServiceRequestCategory.WATER, ServiceRequestImportance.LOW);

        //GARBAGE — IMPORTANT
        put(mapped, "Illegal Dumping Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Needle Clean Up Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.IMPORTANT);

        //GARBAGE — LOW
        put(mapped, "Abandoned Mattress Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Abandoned Recyclables Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Abandoned Non-Recyclables-Large Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Abandoned Non-Recyclables-Small Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Missed Garbage Bin Pickup Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Missed Green Bin Pickup Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Loose Litter Clean Up Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Dead Animal Pick Up Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);
        put(mapped, "Commercial Waste Container Concern Case", ServiceRequestCategory.GARBAGE, ServiceRequestImportance.LOW);

        //SAFETY — IMPORTANT
        put(mapped, "Fire Safety Hazards Case", ServiceRequestCategory.SAFETY, ServiceRequestImportance.IMPORTANT);
        put(mapped, "Environmental Contamination Concern Case", ServiceRequestCategory.SAFETY, ServiceRequestImportance.IMPORTANT);

        //GRAFFITI — LOW
        put(mapped, "Graffiti Removal - City Property Case", ServiceRequestCategory.GRAFFITI, ServiceRequestImportance.LOW);
        put(mapped, "Graffiti Removal - Private Property Case", ServiceRequestCategory.GRAFFITI, ServiceRequestImportance.LOW);
        put(mapped, "Graffiti Removal - Commercial Waste Container Case", ServiceRequestCategory.GRAFFITI, ServiceRequestImportance.LOW);
        put(mapped, "Graffiti Removal - External Organization Case", ServiceRequestCategory.GRAFFITI, ServiceRequestImportance.LOW);
        put(mapped, "Graffiti Management Program Case", ServiceRequestCategory.GRAFFITI, ServiceRequestImportance.LOW);

        //NOISE — LOW
        put(mapped, "Noise on Private Property Case", ServiceRequestCategory.NOISE, ServiceRequestImportance.LOW);

        this.rules = Map.copyOf(mapped);
    }

    public Set<String> allowlistedTypes() {
        return rules.keySet();
    }

    public Optional<Classification> classify(String requestType) {
        if (requestType == null || requestType.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(rules.get(requestType));
    }

    private static void put(
            Map<String, Classification> mapped,
            String requestType,
            ServiceRequestCategory category,
            ServiceRequestImportance importance
    ) {
        mapped.put(requestType, new Classification(category, importance));
    }
}
