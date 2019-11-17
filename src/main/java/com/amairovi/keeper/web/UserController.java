package com.amairovi.keeper.web;

import com.amairovi.keeper.dto.*;
import com.amairovi.keeper.model.Item;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.service.ItemService;
import com.amairovi.keeper.service.UserPlaceService;
import com.amairovi.keeper.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserPlaceService placeService;
    private final UserService userService;
    private final ItemService itemService;

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
        if (actualPassword.equals(expectedPassword)) {
            return user.getId();
        } else {
            // TODO add propert exception handling
            throw new IllegalArgumentException("Wrong password");
        }
    }

    @GetMapping("/{userId}/places")
    public List<UserPlace> getPlacesForUser(@PathVariable String userId) {
        User user = userService.findById(userId);

        return placeService.getPlacesHierarchyForUser(user);
    }

    @GetMapping("/{userId}/items")
    public List<ItemDto> getItemsForUser(@PathVariable String userId) {
        User user = userService.findById(userId);
        Set<String> userPlaceIds = placeService.getPlacesHierarchyForUser(user)
                .stream()
                .flatMap(up -> up.generateHierarchyIds().stream())
                .collect(Collectors.toSet());


        return itemService.getItemsForPlaces(userPlaceIds)
                .stream()
                .map(item -> {
                    ItemDto dto = new ItemDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setPlaceId(item.getPlaceId());
                    return dto;
                }).collect(toList());
    }

    @PutMapping("/{userId}/places/{placeId}")
    public void addPlace(@PathVariable String userId, @PathVariable String placeId) {
        userService.addPlace(userId, placeId);
    }

    @DeleteMapping("/{userId}/places/{placeId}")
    public void deletePlace(@PathVariable String userId, @PathVariable String placeId) {
        userService.deletePlace(userId, placeId);
    }

    @GetMapping("/{userId}/items/recent")
    public List<ItemDto> getRecentItems(@PathVariable String userId) {
        User user = userService.findById(userId);
        return user.getRecentItems()
                .stream()
                .map(itemService::findById)
                .map(item -> {
                    ItemDto dto = new ItemDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setPlaceId(item.getPlaceId());
                    return dto;
                }).collect(toList());
    }

    @PutMapping("/{userId}/items/recent/{itemId}")
    public void addRecentItem(@PathVariable String userId, @PathVariable String itemId) {
        log.debug("Add recent item {} for user {}.", itemId, userId);

        User user = userService.findById(userId);
        Item item = itemService.findById(itemId);

        userService.addItemToRecent(user, item);
    }

    @GetMapping("/{userId}/elements")
    public List<ElementDto> getAllElements(@PathVariable String userId) {
        User user = userService.findById(userId);
        List<ElementDto> result = new ArrayList<>();
        List<UserPlace> rootPlaces = placeService.getPlacesHierarchyForUser(user);

        Set<UserPlace> userPlaces = rootPlaces
                .stream()
                .flatMap(up -> up.generateHierarchy().stream())
                .collect(Collectors.toSet());

        result.addAll(userPlaces
                .stream()
                .map(place -> {
                    ElementDto dto = new ElementDto();
                    dto.setId(place.getId());
                    dto.setName(place.getName());
                    dto.setIsPlace(true);
                    return dto;
                })
                .collect(Collectors.toList()));

        Set<String> userPlaceIds = userPlaces.stream().map(UserPlace::getId).collect(Collectors.toSet());

        result.addAll(itemService.getItemsForPlaces(userPlaceIds)
                .stream()
                .map(item -> {
                    ElementDto dto = new ElementDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setIsPlace(false);
                    return dto;
                })
                .collect(Collectors.toList()));

        return result;
    }

}
