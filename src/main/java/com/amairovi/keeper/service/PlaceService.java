package com.amairovi.keeper.service;

import com.amairovi.keeper.exception.PlaceDoesNotExistException;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public Place create(String name, String parentId) {
        Place place = new Place();
        place.setName(name);
        place.setParentId(parentId);
        String placeId = placeRepository.save(place);
        place.setId(placeId);
        return place;
    }

    public void update(String id, String name, String parentId) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceDoesNotExistException("Place with id " + id + " does not exist."));

        place.setName(name);
        place.setParentId(parentId);

        placeRepository.update(place);
    }
}
