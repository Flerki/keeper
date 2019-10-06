package com.amairovi.keeper.repository;

import com.amairovi.keeper.configuration.MongoConfiguration;
import com.amairovi.keeper.model.Place;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

@Repository
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

    public Optional<Place> findById(String id) {
        MongoCollection<Document> places = mongoConfiguration.getPlaceCollection();

        FindIterable<Document> documents = places.find(eq("_id", new ObjectId(id)));

        return Optional.ofNullable(documents.first())
                .map(this::documentToPlace);
    }

    public void update(Place place) {
        MongoCollection<Document> places = mongoConfiguration.getPlaceCollection();
        Document document = new Document()
                .append("name", place.getName())
                .append("parentId", place.getParentId());

        places.replaceOne(eq("_id", new ObjectId(place.getId())), document);
    }

    public List<Place> find(Set<String> ids) {
        MongoCollection<Document> places = mongoConfiguration.getPlaceCollection();

        List<Place> result = new ArrayList<>();

        Set<ObjectId> objectIds = ids.stream()
                .map(ObjectId::new)
                .collect(Collectors.toSet());

        places.find(in("_id", objectIds))
                .forEach((Consumer<? super Document>) doc -> {
                    Place place = documentToPlace(doc);
                    result.add(place);
                });
        return result;
    }


    public List<Place> findByParent(Place parent) {
        MongoCollection<Document> places = mongoConfiguration.getPlaceCollection();

        List<Place> result = new ArrayList<>();

        places.find(eq("parentId", parent.getId()))
                .forEach((Consumer<? super Document>) doc -> {
                    Place place = documentToPlace(doc);
                    result.add(place);
                });
        return result;
    }

    public void delete(String id) {
        mongoConfiguration.getPlaceCollection()
                .deleteOne(eq("_id", new ObjectId(id)));
    }

    private Place documentToPlace(Document d) {
        Place place = new Place();
        place.setId(d.getObjectId("_id").toString());
        place.setName(d.getString("name"));
        place.setParentId(d.getString("parentId"));
        return place;
    }
}
