package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.Account;
import com.example.barclaysassessment.User;
import com.example.barclaysassessment.dao.AccountDao;
import com.example.barclaysassessment.dao.UserDao;
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

import java.util.List;

@RestController
@RequestMapping("/v1")
public class UserController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@Parameter(description = "User ID", required = true) @PathVariable Long id) {
        User user = userDao.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found.");
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create new user", description = "Creates a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Parameter(description = "User details", required = true)
            @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Required fields are missing.");
        }
        User userInfo = userDao.save(user);
        return ResponseEntity.status(200).body(userInfo);
    }

    @Operation(summary = "Update user", description = "Updates an existing user's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated user details", required = true)
            @RequestBody User user) {
        User existingUser = userDao.findById(id).orElse(null);
        existingUser.setName(user.getName());
        return ResponseEntity.status(200).body(userDao.save(existingUser));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        User user = userDao.findById(id).orElse(null);
        if (user != null) {
            if (accountDao.findByUser(user).isEmpty()) { // no account associated with the user
                userDao.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(409).body("Cannot delete user: user has a bank account.");
            }
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }
}
