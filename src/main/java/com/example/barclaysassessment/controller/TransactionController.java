package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.Account;
import com.example.barclaysassessment.Transaction;
import com.example.barclaysassessment.User;
import com.example.barclaysassessment.dao.AccountDao;
import com.example.barclaysassessment.dao.TransactionDao;
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
public class TransactionController {
    @Autowired
    AccountDao accountDao;

    @Autowired
    TransactionDao transactionDao;

    @Operation(summary = "Create a transaction", description = "Create a deposit or withdrawal transaction for an account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction successful",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(example = "Transaction successful. New Balance: 1000.00"))),
        @ApiResponse(responseCode = "404", description = "Account not found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> transaction(
            @Parameter(description = "Account ID", required = true) @PathVariable String accountId,
            @Parameter(description = "Transaction details", required = true, schema = @Schema(implementation = Transaction.class))
            @RequestBody Transaction transaction) {
        Account acc = accountDao.findByAccountNumber(accountId).orElse(null);
        if(acc != null) {
            Double currentAmount = acc.getBalance();
            Transaction transactionNew = new Transaction();
            if(transaction.getType().equals("deposit")) {
                acc.setBalance(currentAmount + transaction.getAmount());
            }else if(transaction.getType().equals("withdraw")){
                acc.setBalance(currentAmount - transaction.getAmount());
            }
            transactionNew.setAccountNumber(accountId);
            transactionNew.setAmount(transaction.getAmount());
            transactionNew.setType(transaction.getType());
            transactionDao.save(transactionNew);
            accountDao.save(acc);
            return ResponseEntity.ok("Transaction successful. New Balance: " + acc.getBalance());
        }else{
            return ResponseEntity.status(404).body("This account number does not exist");
        }

    }

    @Operation(summary = "Get transactions", description = "Get all transactions for an account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(
            @Parameter(description = "Account ID", required = true) @PathVariable String accountId) {
        List<Transaction> transactions= transactionDao.findByAccountNumber(accountId);

        return ResponseEntity.ok(transactions);
    }
}
