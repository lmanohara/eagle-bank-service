
package com.eaglebank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String name;

    @Embedded
    private Address address;

    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
}
