
package com.eaglebank.service;

import com.eaglebank.dto.UserRequest;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.User;
import com.eaglebank.exception.BadRequestException;
import com.eaglebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public User createUser(UserRequest req) {
        if (req.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("Email exists");
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPhoneNumber(req.getPhoneNumber());

        if (req.getAddress() != null) {
            var a = req.getAddress();
            Address addr = new Address(
                    a.getLine1(), a.getLine2(), a.getLine3(), a.getTown(), a.getCounty(), a.getPostcode()
            );
            u.setAddress(addr);
        }

        // username/password are set via a separate auth endpoint
        u.setCreatedTimestamp(Instant.now());
        u.setUpdatedTimestamp(Instant.now());
        return repo.save(u);
    }
}
