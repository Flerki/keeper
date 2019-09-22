package com.amairovi.keeper.repository;

import com.amairovi.keeper.Application;
import com.amairovi.keeper.model.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class PlaceRepository {
    private List<Place> places = new ArrayList<>();

    public String save(Place place) {
        String id = Application.nextId();
        place.setId(id);

        places.add(place);

        return id;
    }

    public List<Place> find(Set<String> ids) {
        return places.stream()
                .filter(p -> ids.contains(p.getId()))
                .collect(toList());
    }


    public List<Place> findByParent(Place parent) {
        String parentId = parent.getId();
        return places.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .collect(toList());
    }
}
