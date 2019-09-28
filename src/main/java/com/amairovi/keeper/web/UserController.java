package com.amairovi.keeper.web;

import com.amairovi.keeper.dto.Registration;
import com.amairovi.keeper.dto.UserPlace;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.service.PlaceService;
import com.amairovi.keeper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final PlaceService placeService;
    private final UserService userService;

    @PutMapping
    public void register(@RequestBody Registration registration) {
        userService.register(registration.getEmail());
    }

    @GetMapping("/{userId}/places")
    public List<UserPlace> getPlacesForUser(@PathVariable String userId) {
        User user = userService.findById(userId);

        return placeService.getPlacesHierarchyForUser(user);
    }
}
