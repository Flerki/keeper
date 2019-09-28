package com.amairovi.keeper.service;

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

    public void register(String email){
        User user = new User();
        user.setEmail(email);
        userRepository.save(user);
    }
}
