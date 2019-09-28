package com.amairovi.keeper.repository;

import com.amairovi.keeper.configuration.MongoConfiguration;
import com.amairovi.keeper.model.User;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepository {

    private final MongoConfiguration mongoConfiguration;

    public String save(User user){
        log.debug("Save user: {}", user);
        MongoCollection<Document> places = mongoConfiguration.getUserCollection();
        Document document = new Document()
                .append("email", user.getEmail())
                .append("places", user.getPlaces());

        places.insertOne(document);

        return document.get("_id").toString();
    }

}
