package com.aroundvan.backend.environment.aqhi.dto;

import com.aroundvan.backend.environment.aqhi.AqhiRiskLevel;

import java.time.Instant;

public record AqhiResponse(String regionId, String regionName, String neighbourhood, Double value, AqhiRiskLevel riskLevel, String riskLabel, String healthMessage, Instant observedAt
) {
}
