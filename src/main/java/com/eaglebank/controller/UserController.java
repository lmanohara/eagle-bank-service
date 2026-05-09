package com.eaglebank.controller;

import com.eaglebank.dto.AddressDto;
import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @PostMapping
  public UserResponse create(@Valid @RequestBody UserRequest req) {
    UserResponse user = service.createUser(req);

    AddressDto addr = null;
    if (user.getAddress() != null) {
      addr =
          AddressDto.builder()
              .line1(user.getAddress().getLine1())
              .line2(user.getAddress().getLine2())
              .line3(user.getAddress().getLine3())
              .town(user.getAddress().getTown())
              .county(user.getAddress().getCounty())
              .postcode(user.getAddress().getPostcode())
              .build();
    }

    return UserResponse.builder()
        .id(user.getId() == null ? null : user.getId().toString())
        .name(user.getName())
        .address(addr)
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .createdTimestamp(user.getCreatedTimestamp())
        .updatedTimestamp(user.getUpdatedTimestamp())
        .build();
  }
}
