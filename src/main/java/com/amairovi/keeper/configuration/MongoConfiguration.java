package com.amairovi.keeper.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConfiguration {

    private MongoClient mongo;

    public void start() {
        mongo = MongoClients.create("mongodb://localhost:27017");
    }

    public MongoCollection<Document> getPlaceCollection() {
        MongoDatabase database = mongo.getDatabase("keeper");

        return database.getCollection("places");
    }
}
