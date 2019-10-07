package com.amairovi.keeper.repository;

import com.amairovi.keeper.model.Place;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequiredArgsConstructor
public class PlaceRepository {

    @Qualifier("places")
    private final MongoCollection<Document> places;

    public String save(Place place) {
        Document document = new Document("name", place.getName())
                .append("parentId", place.getParentId());

        places.insertOne(document);

        return document.get("_id").toString();
    }

    public Optional<Place> findById(String id) {
        FindIterable<Document> documents = places.find(eq("_id", new ObjectId(id)));

        return Optional.ofNullable(documents.first())
                .map(this::documentToPlace);
    }

    public void update(Place place) {
        Document document = new Document()
                .append("name", place.getName())
                .append("parentId", place.getParentId());

        places.replaceOne(eq("_id", new ObjectId(place.getId())), document);
    }

    public List<Place> find(Set<String> ids) {
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
        List<Place> result = new ArrayList<>();

        places.find(eq("parentId", parent.getId()))
                .forEach((Consumer<? super Document>) doc -> {
                    Place place = documentToPlace(doc);
                    result.add(place);
                });
        return result;
    }

    public void delete(String id) {
        places.deleteOne(eq("_id", new ObjectId(id)));
    }

    private Place documentToPlace(Document d) {
        Place place = new Place();
        place.setId(d.getObjectId("_id").toString());
        place.setName(d.getString("name"));
        place.setParentId(d.getString("parentId"));
        return place;
    }
}
