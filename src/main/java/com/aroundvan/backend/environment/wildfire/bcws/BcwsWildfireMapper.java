package com.aroundvan.backend.environment.wildfire.bcws;

import com.aroundvan.backend.environment.wildfire.Wildfire;
import com.aroundvan.backend.environment.wildfire.bcws.dto.BcwsFireQueryResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BcwsWildfireMapper {

    public void applyToWildfire(
            Wildfire wildfire,
            BcwsFireQueryResponse.Attributes attributes,
            Instant syncedAt
    ) {
        wildfire.setFireNumber(attributes.fireNumber());
        wildfire.setBcFireId(attributes.fireId());
        wildfire.setIncidentName(attributes.incidentName());
        wildfire.setGeographicDescription(attributes.geographicDescription());
        wildfire.setCurrentSizeHectares(attributes.currentSize());
        wildfire.setStatus(attributes.fireStatus());
        wildfire.setCause(attributes.fireCause());
        wildfire.setResponseType(attributes.responseTypeDescription());
        wildfire.setFireCentreCode(attributes.fireCentre());
        wildfire.setZoneCode(attributes.zone());
        wildfire.setFireType(attributes.fireType());
        wildfire.setIgnitionDate(toInstant(attributes.ignitionDate()));
        wildfire.setFireOutDate(toInstant(attributes.fireOutDate()));
        wildfire.setFireOfNote(isYes(attributes.fireOfNoteIndicator()));
        wildfire.setWasFireOfNote(isYes(attributes.wasFireOfNoteIndicator()));
        wildfire.setFireUrl(attributes.fireUrl());
        wildfire.setLastSyncedAt(syncedAt);
    }

    private Instant toInstant(Long epochMilliseconds) {
        return epochMilliseconds == null ? null : Instant.ofEpochMilli(epochMilliseconds);
    }

    private boolean isYes(String indicator) {
        return "Y".equalsIgnoreCase(indicator);
    }
}
