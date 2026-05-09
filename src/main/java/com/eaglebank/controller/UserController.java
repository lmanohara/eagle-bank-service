
package com.eaglebank.controller;

import com.eaglebank.dto.UserRequest;
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
    public String create(@Valid @RequestBody UserRequest req) {
        service.createUser(req);
        return "User created";
    }
}
