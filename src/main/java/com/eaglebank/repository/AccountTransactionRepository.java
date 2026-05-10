package com.eaglebank.repository;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.AccountTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, String> {
  List<AccountTransaction> findByAccount(Account account);

  Optional<AccountTransaction> findByIdAndAccount(String id, Account account);
}
