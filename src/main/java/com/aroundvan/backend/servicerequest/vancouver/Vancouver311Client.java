package com.aroundvan.backend.servicerequest.vancouver;

import com.aroundvan.backend.servicerequest.ServiceRequestTypeRules;
import com.aroundvan.backend.servicerequest.vancouver.dto.Vancouver311RecordsResponse;
import com.aroundvan.backend.servicerequest.vancouver.dto.Vancouver311RecordsResponse.Vancouver311Record;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Vancouver311Client {

    private static final int MAX_PAGES = 50;

    private final RestClient vancouver311RestClient;
    private final Vancouver311Properties properties;
    private final ServiceRequestTypeRules typeRules;

    public List<Vancouver311Record> fetchOpenAllowlistedRecords() {
        return fetchOpenAllowlistedRecords(typeRules.allowlistedTypes());
    }

    public List<Vancouver311Record> fetchOpenAllowlistedRecords(Collection<String> requestTypes) {
        if (requestTypes == null || requestTypes.isEmpty()) {
            return List.of();
        }

        String where = buildWhereClause(requestTypes);
        List<Vancouver311Record> records = new ArrayList<>();

        int pageSize = properties.pageSizeOrDefault();
        int offset = 0;

        for (int page = 0; page < MAX_PAGES; page++) {
            Vancouver311RecordsResponse response = fetchPage(where, offset, pageSize);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                break;
            }

            records.addAll(response.results());

            if (response.results().size() < pageSize) {
                break;
            }

            offset += pageSize;
        }

        return records;
    }

    private Vancouver311RecordsResponse fetchPage(String where, int offset, int pageSize) {
        String dataset = properties.datasetOrDefault();

        return vancouver311RestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/catalog/datasets/{dataset}/records")
                        .queryParam("where", where)
                        .queryParam("limit", pageSize)
                        .queryParam("offset", offset)
                        .queryParam("order_by", "service_request_open_timestamp DESC")
                        .build(dataset)
                )
                .retrieve()
                .body(Vancouver311RecordsResponse.class);
    }

    static String buildWhereClause(Collection<String> requestTypes) {
        String typeList = requestTypes.stream()
                .map(type -> "\"" + type.replace("\"", "") + "\"")
                .collect(Collectors.joining(", "));

        return "status=\"Open\" AND address IS NOT NULL AND latitude IS NOT NULL"
                + " AND longitude IS NOT NULL AND service_request_type IN (" + typeList + ")";
    }
}
