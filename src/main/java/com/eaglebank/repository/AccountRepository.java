package com.eaglebank.repository;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
  List<Account> findByUser(User user);
}
