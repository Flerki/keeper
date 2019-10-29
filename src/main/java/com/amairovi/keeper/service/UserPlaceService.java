package com.amairovi.keeper.service;

import com.amairovi.keeper.dto.UserPlace;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.repository.PlaceRepository;
import com.amairovi.keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class UserPlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public void save(Place place, User user) {
        if (place.getParentId() == null) {
            user.addPlace(place.getId());
        }

        userRepository.update(user);
    }

    public List<UserPlace> getPlacesHierarchyForUser(User user) {
        Set<String> rootPlaceIds = user.getPlaces();
        List<Place> places = placeRepository.find(rootPlaceIds);

        return places.stream()
                .map(p -> createUserPlace(p, null))
                .collect(toList());
    }

    private UserPlace createUserPlace(Place place, String parentId) {
        List<Place> childrenPlaces = placeRepository.findByParent(place);
        String id = place.getId();
        String name = place.getName();
        return childrenPlaces.stream()
                .map(p -> createUserPlace(p, id))
                .collect(collectingAndThen(
                        toList(),
                        childrenUserPlaces -> new UserPlace(id, name, childrenUserPlaces, parentId)
                ));
    }

}
