package com.eaglebank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

  @Id
  @Column(name = "account_id")
  private String id; // format: acc-<uuid>

  @Column(name = "number", unique = true)
  private String accountNumber;

  @Column(name = "sort_code")
  private String sortCode;

  @Column(name = "name")
  private String name;

  @Column(name = "type")
  private String accountType;

  @Column(name = "balance")
  private BigDecimal balance;

  @Column(name = "currency")
  private String currency;

  @Column(name = "created_timestamp")
  private Instant createdTimestamp;

  @Column(name = "updated_timestamp")
  private Instant updatedTimestamp;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_account_user"))
  private User user;
}
