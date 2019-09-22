package com.amairovi.keeper.service;

import com.amairovi.keeper.dto.UserPlace;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.repository.PlaceRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlaceServiceTest {


    private PlaceService placeService;

    private PlaceRepository placeRepository;

    @Before
    public void setup() {
        placeRepository = mock(PlaceRepository.class);
        placeService = new PlaceService(placeRepository);
    }

    @Test
    public void when_user_does_not_have_places_then_return_empty_list() {
        when(placeRepository.find(any())).thenReturn(emptyList());

        User user = new User();

        List<UserPlace> result = placeService.getPlacesHierarchyForUser(user);

        assertThat(result).isEmpty();
    }

    @Test
    public void when_user_has_only_parent_places_then_return_them() {
        User user = new User();
        HashSet<String> placeIds = new HashSet<>(Arrays.asList("1", "2"));
        user.setPlaces(placeIds);

        Place place1 = new Place();
        place1.setId("1");
        place1.setName("name1");

        Place place2 = new Place();
        place2.setId("2");
        place2.setName("name2");

        when(placeRepository.find(eq(user.getPlaces())))
                .thenReturn(Arrays.asList(place1, place2));

        when(placeRepository.findByParent(eq(place1))).thenReturn(emptyList());
        when(placeRepository.findByParent(eq(place2))).thenReturn(emptyList());

        List<UserPlace> result = placeService.getPlacesHierarchyForUser(user);

        assertThat(result).containsExactlyInAnyOrder(
                new UserPlace(place1.getId(), place1.getName(), emptyList()),
                new UserPlace(place2.getId(), place2.getName(), emptyList())
        );
    }

    @Test
    public void when_user_has_hierarchy_then_return_it() {
        User user = new User();
        HashSet<String> placeIds = new HashSet<>(singletonList("1"));
        user.setPlaces(placeIds);

        Place place1 = new Place();
        place1.setId("1");
        place1.setName("name1");

        when(placeRepository.find(eq(user.getPlaces())))
                .thenReturn(singletonList(place1));

        Place place2 = new Place();
        place2.setId("2");
        place2.setName("name2");

        when(placeRepository.findByParent(eq(place1))).thenReturn(singletonList(place2));

        Place place3 = new Place();
        place3.setId("3");
        place3.setName("name3");

        when(placeRepository.findByParent(eq(place2))).thenReturn(singletonList(place3));

        List<UserPlace> result = placeService.getPlacesHierarchyForUser(user);

        assertThat(result).containsOnly(
                new UserPlace(place1.getId(), place1.getName(), singletonList(
                        new UserPlace(place2.getId(), place2.getName(), singletonList(
                                new UserPlace(place3.getId(), place3.getName(), emptyList()))))
                )
        );
    }


}