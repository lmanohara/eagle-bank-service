package com.eaglebank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private AddressDto address;
    private String phoneNumber;
    private String email;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
}
