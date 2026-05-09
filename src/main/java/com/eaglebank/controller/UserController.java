
package com.eaglebank.controller;

import com.eaglebank.dto.AddressDto;
import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.entity.User;
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
        User u = service.createUser(req);

        AddressDto addr = null;
        if (u.getAddress() != null) {
            addr = new AddressDto();
            addr.setLine1(u.getAddress().getLine1());
            addr.setLine2(u.getAddress().getLine2());
            addr.setLine3(u.getAddress().getLine3());
            addr.setTown(u.getAddress().getTown());
            addr.setCounty(u.getAddress().getCounty());
            addr.setPostcode(u.getAddress().getPostcode());
        }

        return new UserResponse(
                u.getId() == null ? null : u.getId().toString(),
                u.getName(),
                addr,
                u.getPhoneNumber(),
                u.getEmail(),
                u.getCreatedTimestamp(),
                u.getUpdatedTimestamp()
        );
    }
}
