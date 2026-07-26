package com.aroundvan.backend.environment.wildfire.fireweather;

import com.aroundvan.backend.environment.wildfire.fireweather.dto.BcwsDailyListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BcwsFireWeatherClient {

    static final ZoneId BCWS_ZONE = ZoneId.of("Etc/GMT+8");

    private static final DateTimeFormatter DATE_HOUR = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RestClient bcwsFireWeatherRestClient;
    private final BcwsFireWeatherProperties properties;

    public List<BcwsDailyListResponse.Daily> fetchRecentDailies(
            double latitude,
            double longitude,
            int radiusKm
    ) {
        LocalDate today = LocalDate.now(BCWS_ZONE);
        String from = today.minusDays(properties.lookbackDaysOrDefault()).format(DATE_HOUR) + "00";
        String to = today.format(DATE_HOUR) + "23";

        BcwsDailyListResponse response = bcwsFireWeatherRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/dailies")
                        .queryParam("point", longitude + "," + latitude)
                        .queryParam("distance", radiusKm)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .queryParam("pageRowCount", properties.pageRowCountOrDefault())
                        .build()
                )
                .retrieve()
                .body(BcwsDailyListResponse.class);

        return response == null || response.collection() == null
                ? List.of()
                : response.collection();
    }
}
