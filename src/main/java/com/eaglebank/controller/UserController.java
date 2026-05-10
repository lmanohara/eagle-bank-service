package com.eaglebank.controller;

import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @PostMapping
  public UserResponse create(@Valid @RequestBody UserRequest userRequest) {
    return service.createUser(userRequest);
  }

  @GetMapping("/{userId}")
  public UserResponse getUser(
      @PathVariable("userId") String userId, @AuthenticationPrincipal String authUsername) {
    return service.getUserById(userId, authUsername);
  }
}
