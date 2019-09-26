package com.amairovi.keeper.repository;

import com.amairovi.keeper.configuration.MongoConfiguration;
import com.amairovi.keeper.model.Place;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlaceRepository {

    private final MongoConfiguration mongoConfiguration;

    public PlaceRepository(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    public String save(Place place) {
        MongoCollection<Document> places = mongoConfiguration.getPlaceCollection();
        Document document = new Document("name", place.getName())
                .append("parentId", place.getParentId());

        places.insertOne(document);

        return document.get("_id").toString();
    }

    public List<Place> find(Set<String> ids) {
        MongoCollection<Document> places = mongoConfiguration.getPlaceCollection();

        List<Place> result = new ArrayList<>();

        Set<ObjectId> objectIds = ids.stream()
                .map(ObjectId::new)
                .collect(Collectors.toSet());
        places.find(Filters.in("_id", objectIds))
                .forEach((Consumer<? super Document>) doc -> {
                    Place place = new Place();
                    place.setName(doc.getString("name"));
                    result.add(place);
                });
        return result;
    }


    public List<Place> findByParent(Place parent) {

        return null;
//        String parentId = parent.getId();
//        return places.stream()
//                .filter(p -> p.getParentId().equals(parentId))
//                .collect(toList());
    }
}
