package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.Account;
import com.example.barclaysassessment.Transaction;
import com.example.barclaysassessment.dao.AccountDao;
import com.example.barclaysassessment.dao.TransactionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    private TransactionController transactionController;

    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAccount = new Account();
        testAccount.setAccountNumber("ACC123");
        testAccount.setBalance(1000.0);

        testTransaction = new Transaction();
        testTransaction.setTransactionId(1L);
        testTransaction.setAccountNumber("ACC123");
        testTransaction.setType("deposit");
        testTransaction.setAmount(500.0);
    }

    @Test
    void transaction_DepositSuccess() {
        when(accountDao.findByAccountNumber("ACC123")).thenReturn(Optional.of(testAccount));
        when(accountDao.save(any(Account.class))).thenReturn(testAccount);
        when(transactionDao.save(any(Transaction.class))).thenReturn(testTransaction);

        ResponseEntity<?> response = transactionController.transaction("ACC123", testTransaction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction successful"));
        assertEquals(1500.0, testAccount.getBalance()); // Original 1000 + 500 deposit
        verify(transactionDao, times(1)).save(any(Transaction.class));
    }

    @Test
    void transaction_WithdrawSuccess() {
        testTransaction.setType("withdraw");
        when(accountDao.findByAccountNumber("ACC123")).thenReturn(Optional.of(testAccount));
        when(accountDao.save(any(Account.class))).thenReturn(testAccount);
        when(transactionDao.save(any(Transaction.class))).thenReturn(testTransaction);

        ResponseEntity<?> response = transactionController.transaction("ACC123", testTransaction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction successful"));
        assertEquals(500.0, testAccount.getBalance()); // Original 1000 - 500 withdrawal
        verify(transactionDao, times(1)).save(any(Transaction.class));
    }

    @Test
    void transaction_AccountNotFound() {
        when(accountDao.findByAccountNumber("ACC123")).thenReturn(Optional.empty());

        ResponseEntity<?> response = transactionController.transaction("ACC123", testTransaction);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("This account number does not exist", response.getBody());
        verify(transactionDao, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactions_Success() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionDao.findByAccountNumber("ACC123")).thenReturn(transactions);

        ResponseEntity<?> response = transactionController.getTransactions("ACC123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Transaction> returnedTransactions = (List<Transaction>) response.getBody();
        assertEquals(1, returnedTransactions.size());
        assertEquals(testTransaction.getTransactionId(), returnedTransactions.get(0).getTransactionId());
    }

    @Test
    void getTransactions_EmptyList() {
        when(transactionDao.findByAccountNumber("ACC123")).thenReturn(Arrays.asList());

        ResponseEntity<?> response = transactionController.getTransactions("ACC123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Transaction> returnedTransactions = (List<Transaction>) response.getBody();
        assertTrue(returnedTransactions.isEmpty());
    }
}
