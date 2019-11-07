package com.amairovi.keeper.configuration;

import com.amairovi.keeper.model.Item;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(name = "generate-data", havingValue = "true")
@Slf4j
public class InitialDataConfiguration {

    private final MongoCollection<Document> placesCollection;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> itemsCollection;

    public InitialDataConfiguration(
            @Qualifier("places") MongoCollection<Document> placesCollection,
            @Qualifier("users") MongoCollection<Document> usersCollection,
            @Qualifier("items") MongoCollection<Document> itemsCollection
    ) {
        this.placesCollection = placesCollection;
        this.usersCollection = usersCollection;
        this.itemsCollection = itemsCollection;
    }

    @PostConstruct
    public void init() {
        log.info("Generate and upload some data to db");

        addPlaces();
        addItems();
        addUsers();
    }

    @Data
    private static class PlaceData {

        private String id;
        private String name;
        private PlaceData[] children;

    }

    private void addPlaces() {
        log.info("Begin to generate and upload some places to db");

        Resource resource = new ClassPathResource("data/places.json");
        try (InputStream input = resource.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String json = reader.lines().collect(Collectors.joining());
            ObjectMapper objectMapper = new ObjectMapper();
            PlaceData[] placeData = objectMapper.readValue(json, PlaceData[].class);
            List<Place> places = convertPlaceDataToPlace(placeData, null);

            placesCollection.drop();

            places.forEach(p -> {

                        Document document = new Document("name", p.getName())
                                .append("_id", new ObjectId(p.getId()))
                                .append("parentId", p.getParentId());

                        placesCollection.insertOne(document);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("End to generate and upload some places to db");
    }

    private List<Place> convertPlaceDataToPlace(PlaceData[] placeDataArr, String parentId) {
        if (placeDataArr == null){
            return Collections.emptyList();
        }

        ArrayList<Place> places = new ArrayList<>();

        for (PlaceData placeData : placeDataArr) {
            Place place = new Place();
            place.setId(placeData.getId());
            place.setName(placeData.getName());
            place.setParentId(parentId);

            places.add(place);
            List<Place> children = convertPlaceDataToPlace(placeData.getChildren(), place.getId());

            places.addAll(children);

        }

        return places;
    }

    private void addItems() {
        log.info("Begin to generate and upload some items to db");

        Resource resource = new ClassPathResource("data/items.json");
        try (InputStream input = resource.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String json = reader.lines().collect(Collectors.joining());
            ObjectMapper objectMapper = new ObjectMapper();
            Item[] places = objectMapper.readValue(json, Item[].class);

            itemsCollection.drop();

            Arrays.stream(places)
                    .forEach(p -> {

                        Document document = new Document("name", p.getName())
                                .append("_id", new ObjectId(p.getId()))
                                .append("placeId", p.getPlaceId());

                        itemsCollection.insertOne(document);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("End to generate and upload some items to db");
    }


    private void addUsers() {
        log.info("Begin to generate and upload some users to db");

        Resource resource = new ClassPathResource("data/users.json");
        try (InputStream input = resource.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String json = reader.lines().collect(Collectors.joining());
            ObjectMapper objectMapper = new ObjectMapper();
            User[] users = objectMapper.readValue(json, User[].class);

            usersCollection.drop();

            Arrays.stream(users)
                    .forEach(u -> {

                        Document document = new Document()
                                .append("_id", new ObjectId(u.getId()))
                                .append("email", u.getEmail())
                                .append("password", u.getPassword())
                                .append("places", u.getPlaces());

                        usersCollection.insertOne(document);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("End to generate and upload some users to db");

    }

}
