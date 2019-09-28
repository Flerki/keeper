package com.amairovi.keeper.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class MongoConfiguration {

    private MongoClient mongo;

    @PostConstruct
    public void start() {
        mongo = MongoClients.create("mongodb://localhost:27017");
        log.debug("Client to mongo was successfully created");
    }

    public MongoCollection<Document> getPlaceCollection() {
        MongoDatabase database = mongo.getDatabase("keeper");

        return database.getCollection("places");
    }

    public MongoCollection<Document> getUserCollection() {
        MongoDatabase database = mongo.getDatabase("keeper");

        return database.getCollection("users");
    }
}
