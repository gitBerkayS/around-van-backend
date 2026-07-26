package com.aroundvan.backend.location.neighbourhood;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NeighbourhoodSeeder implements ApplicationRunner {

    private static final int SRID_WGS84 = 4326;

    private static final String GEOJSON_PATH =
            "data/vancouver-neighbourhoods.geojson";

    private final NeighbourhoodRepository neighbourhoodRepository;
    private final ObjectMapper objectMapper;

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), SRID_WGS84);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (neighbourhoodRepository.count() > 0) {
            return;
        }

        List<Neighbourhood> neighbourhoods = parseNeighbourhoods();

        neighbourhoodRepository.saveAll(neighbourhoods);

        log.info(
                "Seeded {} Vancouver neighbourhoods",
                neighbourhoods.size()
        );
    }

    private List<Neighbourhood> parseNeighbourhoods() throws Exception {
        ClassPathResource resource = new ClassPathResource(GEOJSON_PATH);

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode features = root.path("features");

            List<Neighbourhood> neighbourhoods = new ArrayList<>();

            for (JsonNode feature : features) {
                JsonNode geometry = feature.path("geometry");

                if (!"Polygon".equals(geometry.path("type").asText())) {
                    continue;
                }

                String name = feature
                        .path("properties")
                        .path("name")
                        .asText(null);

                Polygon boundary = toPolygon(geometry.path("coordinates"));

                Neighbourhood neighbourhood = new Neighbourhood();
                neighbourhood.setName(name);
                neighbourhood.setBoundary(boundary);
                neighbourhood.setMunicipality(Municipality.VANCOUVER);

                neighbourhoods.add(neighbourhood);
            }

            return neighbourhoods;
        }
    }

    private Polygon toPolygon(JsonNode rings) {
        LinearRing shell = toLinearRing(rings.get(0));

        LinearRing[] holes = new LinearRing[Math.max(0, rings.size() - 1)];
        for (int i = 1; i < rings.size(); i++) {
            holes[i - 1] = toLinearRing(rings.get(i));
        }

        Polygon polygon = geometryFactory.createPolygon(shell, holes);
        polygon.setSRID(SRID_WGS84);

        return polygon;
    }

    private LinearRing toLinearRing(JsonNode ring) {
        Coordinate[] coordinates = new Coordinate[ring.size()];

        for (int i = 0; i < ring.size(); i++) {
            JsonNode point = ring.get(i);

            double longitude = point.get(0).asDouble();
            double latitude = point.get(1).asDouble();

            coordinates[i] = new Coordinate(longitude, latitude);
        }

        return geometryFactory.createLinearRing(coordinates);
    }
}
