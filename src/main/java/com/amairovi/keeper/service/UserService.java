package com.amairovi.keeper.service;

import com.amairovi.keeper.exception.UserDoesNotExistException;
import com.amairovi.keeper.model.Item;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final static int AMOUNT_OF_RECENT_ITEMS = 10;

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

    public void addPlace(String userId, String placeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserDoesNotExistException("User with id " + userId + " doesn't exist."));

        user.getPlaces().add(placeId);

        userRepository.update(user);
    }

    public void deletePlace(String userId, String placeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserDoesNotExistException("User with id " + userId + " doesn't exist."));

        user.getPlaces().remove(placeId);

        userRepository.update(user);
    }

    public void addItemToRecent(User user, Item item) {
        List<String> recentItems = user.getRecentItems();
        String itemId = item.getId();
        recentItems.removeIf(id -> id.equals(itemId));
        recentItems.add(0, itemId);

        if (recentItems.size() > AMOUNT_OF_RECENT_ITEMS) {
            recentItems.remove(AMOUNT_OF_RECENT_ITEMS);
        }

        userRepository.update(user);
    }

}
