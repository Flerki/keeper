package com.amairovi.keeper.service;

import com.amairovi.keeper.exception.UserDoesNotExistException;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserDoesNotExistException("User with email " + email + " doesn't exist."));
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserDoesNotExistException("User with id " + id + " doesn't exist."));
    }
}
