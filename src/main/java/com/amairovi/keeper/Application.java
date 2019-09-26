package com.amairovi.keeper;

import com.amairovi.keeper.configuration.MongoConfiguration;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.repository.PlaceRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {
    private static AtomicInteger counter = new AtomicInteger();

    public static String nextId(){
        return ((Integer) counter.incrementAndGet()).toString();
    }

    public static void main(String[] args) throws IOException {
        MongoConfiguration mongoConfiguration = new MongoConfiguration();
        mongoConfiguration.start();

        PlaceRepository placeRepository = new PlaceRepository(mongoConfiguration);

        Place place = new Place();
        place.setName("name_1");
        String id = placeRepository.save(place);

        System.out.println(id);

        List<String> ids = new ArrayList<>(
                Arrays.asList("5d8909dd1eaec5330b4e5973", "5d890a0ad443da11be3cb8de"));
        ids.add(id);
        List<Place> places = placeRepository.find(new HashSet<>(ids));

        System.out.println(places);

    }
}
