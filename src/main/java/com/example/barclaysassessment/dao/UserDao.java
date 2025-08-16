package com.example.barclaysassessment.dao;

import com.example.barclaysassessment.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    // Additional query methods can be defined here
}

