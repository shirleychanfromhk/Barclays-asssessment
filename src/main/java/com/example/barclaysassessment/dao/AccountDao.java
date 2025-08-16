package com.example.barclaysassessment.dao;

import com.example.barclaysassessment.Account;
import com.example.barclaysassessment.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDao extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
    List<Account> findByUserId(Long id);
    Optional<Account> findByAccountNumber(String accountNumber);
}

