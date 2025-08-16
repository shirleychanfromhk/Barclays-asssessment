package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.Account;
import com.example.barclaysassessment.dao.AccountDao;
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
public class AccountController {
    @Autowired
    private AccountDao accountDao;

    @Operation(summary = "Create account", description = "Creates a new bank account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account created successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(
            @Parameter(description = "Account details", required = true)
            @RequestBody Account account) {
        if (account.getAccountNumber().isEmpty() || account.getAccountType().isEmpty() || account.getUser() == null) {
            return ResponseEntity.badRequest().body("Required fields are missing.");
        }
        Account savedAccount = accountDao.save(account);
        return ResponseEntity.status(200).body(savedAccount);
    }

    @Operation(summary = "Get accounts by user ID", description = "Retrieves all accounts for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "No accounts found")
    })
    @GetMapping("/accounts/{id}")
    public ResponseEntity<?> getAccountById(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        List<Account> accounts = accountDao.findByUserId(id);
        if (accounts == null || accounts.isEmpty()) {
            return ResponseEntity.status(404).body("Account not found.");
        }
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Update account", description = "Updates an existing account's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PatchMapping("/accounts/{id}")
    public ResponseEntity<?> updateAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated account details", required = true)
            @RequestBody Account account) {
        Account existingAccount = accountDao.findById(id).orElse(null);
        if (existingAccount == null) {
            return ResponseEntity.status(404).body("Account not found.");
        }
        if (account.getAccountNumber() != null && !account.getAccountNumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Account number cannot be updated.");
        }
        if (account.getAccountType() != null && !account.getAccountType().isEmpty()) {
            existingAccount.setAccountType(account.getAccountType());
        }
        if (account.getBalance() != 0) {
            existingAccount.setBalance(account.getBalance());
        }
        if (account.getUser() != null) {
            existingAccount.setUser(account.getUser());
        }
        Account updatedAccount = accountDao.save(existingAccount);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Delete account", description = "Deletes an account by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id) {
        Account account = accountDao.findById(id).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Account not found.");
        }
        accountDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
