package com.aroundvan.backend.location;

import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import com.aroundvan.backend.location.neighbourhood.NeighbourhoodService;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final NeighbourhoodService neighbourhoodService;

    // Resolves imported coordinates into a persisted Location, reusing the
    // event's existing Location on re-import so we don't orphan rows
    // Returns null when coordinates are missing so nearby filtering can skip.
    public Location resolveEventLocation(Location existingLocation, Double latitude, Double longitude) {
        return resolveCoordinatesLocation(existingLocation, latitude, longitude);
    }

    public Location resolveHomeLocation(
            Location existingLocation,
            double latitude,
            double longitude,
            String postalCodePrefix
    ) {
        Location location = resolveCoordinatesLocation(existingLocation, latitude, longitude);

        if (location != null && postalCodePrefix != null && !postalCodePrefix.isBlank()) {
            location.setPostalCodePrefix(normalizePostalCodePrefix(postalCodePrefix));
            return locationRepository.save(location);
        }

        return location;
    }

    public Location resolveHomeLocation(Location existingLocation, double latitude, double longitude) {
        return resolveCoordinatesLocation(existingLocation, latitude, longitude);
    }

    public Location resolveWildfireLocation(Location existingLocation, Double latitude, Double longitude) {
        return resolveCoordinatesLocation(existingLocation, latitude, longitude);
    }

    public Location resolveGasStationLocation(
            Location existingLocation,
            Double latitude,
            Double longitude
    ) {
        return resolveCoordinatesLocation(existingLocation, latitude, longitude);
    }

    public static String normalizePostalCodePrefix(String postalCode) {
        String compact = postalCode.replaceAll("\\s+", "").toUpperCase();

        if (compact.length() < 3) {
            return compact;
        }

        return compact.substring(0, 3);
    }

    private Location resolveCoordinatesLocation(Location existingLocation, Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return existingLocation;
        }

        Location location = existingLocation != null ? existingLocation : new Location();

        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Neighbourhood neighbourhood = neighbourhoodService
                .findByCoordinates(latitude, longitude)
                .orElse(null);

        location.setNeighbourhood(neighbourhood);

        return locationRepository.save(location);
    }

    //calculate distance from home location to requested LIST of locations.
    // order in order of distance.

    //Haversine Formula to calculate km between 2 points on a map.
    public double calculateDistanceBetweenPointsInKm(Coordinates coordinates1, Coordinates coordinates2){
        double earthRadius = 6371.0;


        double distanceBetweenLat = Math.toRadians(coordinates2.latitude() - coordinates1.latitude());
        double distanceBetweenLong = Math.toRadians(coordinates2.longitude() - coordinates1.longitude());

        double radianLatitude1 = Math.toRadians(coordinates1.latitude());
        double radianLatitude2 = Math.toRadians(coordinates2.latitude());

        //haversine
        double a =
                Math.sin(distanceBetweenLat / 2) * Math.sin(distanceBetweenLat / 2) +
                Math.cos(radianLatitude1) * Math.cos(radianLatitude2) *
                Math.sin(distanceBetweenLong / 2) * Math.sin(distanceBetweenLong / 2);

        double b = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //km conversion
        return earthRadius * b;

    }

    public double calculateDistanceFromUserInKm(User user, Location targetLocation) {
        Location homeLocation = user.getHomeLocation();

        LocationCoordinates homeCoordinates =
                new LocationCoordinates(
                        homeLocation.getLongitude(),
                        homeLocation.getLatitude()
                );

        LocationCoordinates targetCoordinates =
                new LocationCoordinates(
                        targetLocation.getLongitude(),
                        targetLocation.getLatitude()
                );

        return calculateDistanceBetweenPointsInKm(
                homeCoordinates,
                targetCoordinates
        );
    }

    public ArrayList<Location> getClosestLocationsAscendingByDistance(User user, ArrayList<Location> locationList) {

        LocationCoordinates userCoordinates = new LocationCoordinates(user.getHomeLocation().getLongitude(), user.getHomeLocation().getLatitude());

        ArrayList<NearestLocation> closestLocations = new ArrayList<>();


        for (Location location: locationList) {
        LocationCoordinates locationcoordinates = new LocationCoordinates(
                location.getLongitude(),
                location.getLatitude()
        );

             double distance = calculateDistanceBetweenPointsInKm(userCoordinates, locationcoordinates);


            closestLocations.add(new NearestLocation(location, distance));
        }
        closestLocations.sort(Comparator.comparingDouble(NearestLocation::distance));

        ArrayList<Location> sortedLocations = new ArrayList<>();
        for (NearestLocation nearestLocation : closestLocations) {
            sortedLocations.add(nearestLocation.location());
        }

        return sortedLocations;
    }
}
