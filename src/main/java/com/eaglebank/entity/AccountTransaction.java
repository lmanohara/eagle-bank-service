package com.eaglebank.entity;

import com.eaglebank.model.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "account_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransaction {

  @Id
  @Column(name = "transaction_id")
  private String id; // format: trn-<uuid>

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_transaction_account"))
  private Account account;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "currency")
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private TransactionType type;

  @Column(name = "reference")
  private String reference;

  @Column(name = "created_timestamp")
  private Instant createdTimestamp;

  @Column(name = "updated_timestamp")
  private Instant updatedTimestamp;
}
