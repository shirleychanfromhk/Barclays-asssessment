package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.User;
import com.example.barclaysassessment.dao.UserDao;
import com.example.barclaysassessment.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class AuthController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Authenticate user", description = "Authenticates a user by ID and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user ID",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Parameter(description = "User ID to authenticate", required = true, example = "1")
            @RequestParam String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        try {
            User user = userDao.findById(Long.parseLong(userId))
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }

            String token = jwtService.generateToken(userId);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid user ID format");
        }
    }
}

@Schema(description = "Authentication response containing JWT token and user ID")
class AuthResponse {
    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "User ID", example = "1")
    private String userId;

    // Getters and setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
