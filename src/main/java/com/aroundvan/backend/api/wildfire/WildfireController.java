package com.aroundvan.backend.api.wildfire;

import com.aroundvan.backend.environment.wildfire.WildfireService;
import com.aroundvan.backend.environment.wildfire.dto.WildfireResponse;
import com.aroundvan.backend.environment.wildfire.fireweather.FireWeatherService;
import com.aroundvan.backend.environment.wildfire.fireweather.dto.FireWeatherResponse;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/wildfires")
@RequiredArgsConstructor
public class WildfireController {

    private final WildfireService wildfireService;
    private final FireWeatherService fireWeatherService;
    private final UserService userService;

    @GetMapping("/active")
    public List<WildfireResponse> getActiveWildfires() {
        return wildfireService.getActiveWildfires();
    }

    @GetMapping("/active/near")
    public List<WildfireResponse> getActiveWildfiresNearby(
            @RequestParam(required = false) Integer limit
    ) {
        User user = requireUserWithHomeLocation("Set your home location before requesting nearby wildfires");

        return wildfireService.getActiveWildfiresNearbyForUser(user, limit);
    }

    @GetMapping("/fire-weather/near")
    public List<FireWeatherResponse> getFireWeatherNearby(
            @RequestParam(required = false) Integer radiusKm,
            @RequestParam(required = false) Integer limit
    ) {
        User user = requireUserWithHomeLocation("Set your home location before requesting fire weather");

        return fireWeatherService.getFireWeatherNearbyForUser(user, radiusKm, limit);
    }

    private User requireUserWithHomeLocation(String message) {
        User user = userService.getCurrentUser();

        if (user.getHomeLocation() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        return user;
    }
}
