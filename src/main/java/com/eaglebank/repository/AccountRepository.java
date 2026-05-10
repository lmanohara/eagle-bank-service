package com.eaglebank.repository;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
  List<Account> findByUser(User user);

  Optional<Account> findByAccountNumber(String accountNumber);
}
