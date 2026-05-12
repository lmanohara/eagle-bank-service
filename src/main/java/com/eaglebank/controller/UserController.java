package com.eaglebank.controller;

import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @PostMapping
  public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest) {
    return ResponseEntity.ok(service.createUser(userRequest));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUser(@PathVariable("userId") String userId) {
    return ResponseEntity.ok(service.getUserById(userId));
  }
}
