package com.aroundvan.backend.api.aqhi;

import com.aroundvan.backend.environment.aqhi.AqhiService;
import com.aroundvan.backend.environment.aqhi.dto.AqhiResponse;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/aqhi")
@RequiredArgsConstructor
public class AqhiController {

    private final AqhiService aqhiService;
    private final UserService userService;

    @GetMapping("/current")
    public AqhiResponse getCurrentAqhi() {
        User user = userService.getCurrentUser();

        if (user.getHomeLocation() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Set your home location before requesting air quality"
            );
        }

        return aqhiService.getCurrentAqhiForUser(user);
    }
}
