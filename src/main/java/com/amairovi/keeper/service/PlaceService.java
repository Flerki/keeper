package com.amairovi.keeper.service;

import com.amairovi.keeper.dto.UserPlace;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public String save(Place place, User user) {
        String id = placeRepository.save(place);

        if (place.getParentId() == null) {
            user.addPlace(id);
        }

        return id;
    }

    public List<UserPlace> getPlacesHierarchyForUser(User user) {
        Set<String> rootPlaceIds = user.getPlaces();
        List<Place> places = placeRepository.find(rootPlaceIds);

        return places.stream()
                .map(this::createUserPlace)
                .collect(toList());
    }

    private UserPlace createUserPlace(Place place) {
        List<Place> childrenPlaces = placeRepository.findByParent(place);
        String id = place.getId();
        String name = place.getName();
        return childrenPlaces.stream()
                .map(this::createUserPlace)
                .collect(collectingAndThen(
                        toList(),
                        childrenUserPlaces -> new UserPlace(id, name, childrenUserPlaces)
                ));

    }
}
