package com.eaglebank.repository;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.AccountTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
  List<AccountTransaction> findByAccount(Account account);
}
