package com.aroundvan.backend.api.gas;

import com.aroundvan.backend.gas.FuelType;
import com.aroundvan.backend.gas.GasImportService;
import com.aroundvan.backend.gas.GasService;
import com.aroundvan.backend.gas.dto.GasImportRequest;
import com.aroundvan.backend.gas.dto.GasStationResponse;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gas")
@RequiredArgsConstructor
public class GasController {

    private final GasService gasService;
    private final GasImportService gasImportService;
    private final UserService userService;

    @GetMapping("/near")
    public List<GasStationResponse> getNearestStations(
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Integer limit
    ) {
        User user = requireUserWithHomeLocation();
        return gasService.getNearestStations(user, fuelType, limit);
    }

    @GetMapping("/cheapest")
    public List<GasStationResponse> getCheapestStations(
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Integer limit
    ) {
        User user = requireUserWithHomeLocation();
        return gasService.getCheapestStations(user, fuelType, limit);
    }

    @PostMapping("/import")
    public Map<String, Integer> importStations(@Valid @RequestBody GasImportRequest request) {
        int imported = gasImportService.importStations(request);
        return Map.of("imported", imported);
    }

    private User requireUserWithHomeLocation() {
        User user = userService.getCurrentUser();

        if (user.getHomeLocation() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Set your home location before requesting gas stations"
            );
        }

        return user;
    }
}
