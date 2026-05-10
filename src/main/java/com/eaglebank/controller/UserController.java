package com.eaglebank.controller;

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
    return service.createUser(req);
  }

  @GetMapping("/{userId}")
  public UserResponse getUser(@PathVariable("userId") String userId) {
    return service.getUserById(userId);
  }
}
