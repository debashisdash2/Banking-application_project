package com.example.bankingapplication.repo;

import com.example.bankingapplication.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, Integer> {
    
    List<Account> findByAccBalanceLessThan(double accBalance);
    List<Account> findByAccBalanceGreaterThan(double accBalance);
    Optional<Account> findByAccHolderName(String accHolderName);
}