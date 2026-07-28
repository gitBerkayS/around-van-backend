package com.aroundvan.backend.environment.wildfire.fireweather;

import com.aroundvan.backend.environment.wildfire.fireweather.dto.BcwsDailyListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BcwsFireWeatherClient {

    static final ZoneId BCWS_ZONE = ZoneId.of("Etc/GMT+8");

    private static final DateTimeFormatter DATE_HOUR = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MIN_RADIUS_KM = 10;

    private final RestClient bcwsFireWeatherRestClient;
    private final BcwsFireWeatherProperties properties;

    public List<BcwsDailyListResponse.Daily> fetchRecentDailies(
            double latitude,
            double longitude,
            int radiusKm
    ) {
        int radius = Math.max(radiusKm, MIN_RADIUS_KM);
        RestClientResponseException lastTooMany = null;

        while (radius >= MIN_RADIUS_KM) {
            try {
                return fetchDailies(latitude, longitude, radius);
            } catch (RestClientResponseException ex) {
                if (!isTooManyRecords(ex)) {
                    throw mapProviderError(ex);
                }

                lastTooMany = ex;

                if (radius == MIN_RADIUS_KM) {
                    break;
                }

                radius = Math.max(MIN_RADIUS_KM, radius / 2);
            }
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "BCWS fire-weather query matched too many records even at "
                        + MIN_RADIUS_KM
                        + " km; try a smaller radiusKm",
                lastTooMany
        );
    }

    private List<BcwsDailyListResponse.Daily> fetchDailies(
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

    private static boolean isTooManyRecords(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString(StandardCharsets.UTF_8);
        return body != null && body.contains("Too many records");
    }

    private static ResponseStatusException mapProviderError(RestClientResponseException ex) {
        return new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "BCWS fire-weather request failed (" + ex.getStatusCode().value() + ")",
                ex
        );
    }
}
