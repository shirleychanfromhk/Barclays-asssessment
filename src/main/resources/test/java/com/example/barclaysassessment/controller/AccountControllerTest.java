package com.example.barclaysassessment.controller;

import com.example.barclaysassessment.Account;
import com.example.barclaysassessment.User;
import com.example.barclaysassessment.dao.AccountDao;
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

class AccountControllerTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountController accountController;

    private Account testAccount;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");

        testAccount = new Account();
        testAccount.setAccountNumber("ACC123");
        testAccount.setAccountType("SAVINGS");
        testAccount.setBalance(1000.0);
        testAccount.setUser(testUser);
    }

    @Test
    void createAccount_Success() {
        when(accountDao.save(any(Account.class))).thenReturn(testAccount);

        ResponseEntity<?> response = accountController.createAccount(testAccount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Account savedAccount = (Account) response.getBody();
        assertEquals(testAccount.getAccountNumber(), savedAccount.getAccountNumber());
    }

    @Test
    void getAccountById_Success() {
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountDao.findByUserId(1L)).thenReturn(accounts);

        ResponseEntity<?> response = accountController.getAccountById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Account> returnedAccounts = (List<Account>) response.getBody();
        assertEquals(1, returnedAccounts.size());
        assertEquals(testAccount.getAccountNumber(), returnedAccounts.get(0).getAccountNumber());
    }

    @Test
    void getAccountById_NotFound() {
        when(accountDao.findByUserId(1L)).thenReturn(null);

        ResponseEntity<?> response = accountController.getAccountById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found.", response.getBody());
    }

    @Test
    void updateAccount_Success() {
        when(accountDao.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountDao.save(any(Account.class))).thenReturn(testAccount);

        Account updateAccount = new Account();
        updateAccount.setAccountType("CHECKING");
        updateAccount.setBalance(2000.0);

        ResponseEntity<?> response = accountController.updateAccount(1L, updateAccount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateAccount_NotFound() {
        when(accountDao.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = accountController.updateAccount(1L, testAccount);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found.", response.getBody());
    }

    @Test
    void deleteAccount_Success() {
        when(accountDao.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(accountDao).deleteById(1L);

        ResponseEntity<?> response = accountController.deleteAccount(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteAccount_NotFound() {
        when(accountDao.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = accountController.deleteAccount(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found.", response.getBody());
        verify(accountDao, never()).deleteById(anyLong());
    }
}
