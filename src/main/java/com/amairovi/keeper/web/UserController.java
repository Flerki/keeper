package com.amairovi.keeper.web;

import com.amairovi.keeper.dto.Authentication;
import com.amairovi.keeper.dto.Registration;
import com.amairovi.keeper.dto.UserPlace;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.service.UserPlaceService;
import com.amairovi.keeper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserPlaceService placeService;
    private final UserService userService;

    @PutMapping
    public void register(@RequestBody Registration registration) {
        userService.register(registration.getEmail(), registration.getPassword());
    }

    @PostMapping
    public String authentication(@RequestBody Authentication authentication) {
        String email = authentication.getEmail();
        User user = userService.findByEmail(email);

        // TODO: move check to a separate service
        String expectedPassword = user.getPassword();
        String actualPassword = authentication.getPassword();
        if (actualPassword.equals(expectedPassword)){
            return user.getId();
        }else {
            // TODO add propert exception handling
            throw new IllegalArgumentException("Wrong password");
        }
    }

    @GetMapping("/{userId}/places")
    public List<UserPlace> getPlacesForUser(@PathVariable String userId) {
        User user = userService.findById(userId);

        return placeService.getPlacesHierarchyForUser(user);
    }
}
