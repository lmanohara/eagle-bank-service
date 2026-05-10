package com.eaglebank.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "username", unique = true)
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "name")
  private String name;

  @Embedded private Address address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "created_timestamp")
  private Instant createdTimestamp;

  @Column(name = "updated_timestamp")
  private Instant updatedTimestamp;
}
