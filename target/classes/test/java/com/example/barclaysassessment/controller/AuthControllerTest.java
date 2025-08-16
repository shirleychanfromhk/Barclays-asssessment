package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.User;
import com.example.barclaysassessment.dao.UserDao;
import com.example.barclaysassessment.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private Map<String, String> credentials;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        credentials = new HashMap<>();
        credentials.put("userId", "1");

        when(jwtService.generateToken(anyString())).thenReturn("test.jwt.token");
    }

    @Test
    void authenticate_Success() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authController.authenticate(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody.get("token"));
        assertEquals("1", responseBody.get("userId"));
    }

    @Test
    void authenticate_MissingUserId() {
        Map<String, String> emptyCredentials = new HashMap<>();

        ResponseEntity<?> response = authController.authenticate(emptyCredentials);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User ID is required", response.getBody());
    }

    @Test
    void authenticate_InvalidUserId() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticate(credentials);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user ID", response.getBody());
    }

    @Test
    void authenticate_InvalidUserIdFormat() {
        Map<String, String> invalidCredentials = new HashMap<>();
        invalidCredentials.put("userId", "invalid");

        ResponseEntity<?> response = authController.authenticate(invalidCredentials);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user ID format", response.getBody());
    }
}
