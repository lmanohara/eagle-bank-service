
package com.eaglebank.service;

import com.eaglebank.dto.UserRequest;
import com.eaglebank.entity.User;
import com.eaglebank.exception.BadRequestException;
import com.eaglebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public User createUser(UserRequest req) {
        if (repo.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("Username exists");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));

        return repo.save(u);
    }
}
