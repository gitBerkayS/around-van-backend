package com.aroundvan.backend.environment.wildfire.bcws;

import com.aroundvan.backend.environment.wildfire.bcws.dto.BcwsFireQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BcwsWildfireClient {

    private static final int MAX_PAGES = 20;

    private static final String ACTIVE_FIRES_FILTER = "FIRE_STATUS <> 'Out'";

    private final RestClient bcwsWildfireRestClient;
    private final BcwsWildfireProperties properties;

    public List<BcwsFireQueryResponse.Attributes> fetchActiveFires() {
        List<BcwsFireQueryResponse.Attributes> fires = new ArrayList<>();

        int pageSize = properties.pageSizeOrDefault();
        int offset = 0;

        for (int page = 0; page < MAX_PAGES; page++) {
            BcwsFireQueryResponse response = fetchPage(offset, pageSize);

            if (response == null || response.features() == null
                    || response.features().isEmpty()) {
                break;
            }

            response.features().stream()
                    .map(BcwsFireQueryResponse.Feature::attributes)
                    .filter(attributes -> attributes != null)
                    .forEach(fires::add);

            if (!Boolean.TRUE.equals(response.exceededTransferLimit())) {
                break;
            }

            offset += pageSize;
        }

        return fires;
    }

    private BcwsFireQueryResponse fetchPage(int offset, int pageSize) {
        return bcwsWildfireRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("where", ACTIVE_FIRES_FILTER)
                        .queryParam("outFields", "*")
                        .queryParam("returnGeometry", "false")
                        .queryParam("resultOffset", offset)
                        .queryParam("resultRecordCount", pageSize)
                        .queryParam("f", "json")
                        .build()
                )
                .retrieve()
                .body(BcwsFireQueryResponse.class);
    }
}
