package com.amairovi.keeper.web;

import com.amairovi.keeper.configuration.MongoConfiguration;
import com.amairovi.keeper.dto.Registration;
import com.amairovi.keeper.dto.UserPlace;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.service.PlaceService;
import com.amairovi.keeper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final PlaceService placeService;
    private final UserService userService;
    private final MongoConfiguration mongoConfiguration;

    @PutMapping
    public void register(@RequestBody Registration registration) {
        userService.register(registration.getEmail());
    }

    @GetMapping("/{userId}/places")
    public List<UserPlace> getPlacesForUser(@PathVariable String userId) {
        Set<String> places = new HashSet<>();

        mongoConfiguration.getPlaceCollection().find(eq("parentId", null))
                .forEach((Consumer<? super Document>) doc -> {
                    ObjectId id = doc.getObjectId("_id");
                    places.add(id.toString());
                });

        User user = new User();
        user.setPlaces(places);

        return placeService.getPlacesHierarchyForUser(user);
    }
}
