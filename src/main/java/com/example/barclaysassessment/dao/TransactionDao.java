package com.example.barclaysassessment.dao;

import com.example.barclaysassessment.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t JOIN Account a ON t.accountNumber = a.accountNumber WHERE t.accountNumber = :accountNumber")
    List<Transaction> findAllByAccountNumber(@Param("accountNumber") String accountNumber);

    // Simple method without join if you just need transactions
    List<Transaction> findByAccountNumber(String accountNumber);
}