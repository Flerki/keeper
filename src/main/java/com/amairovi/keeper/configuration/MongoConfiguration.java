package com.amairovi.keeper.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MongoConfiguration {

    @Bean
    public MongoClient mongoClient() {
        MongoClient mongo = MongoClients.create("mongodb://localhost:27017");
        log.debug("Client to mongo was successfully created");
        return mongo;
    }

    @Bean
    public MongoDatabase mongoDatabase() {
        return mongoClient().getDatabase("keeper");
    }

    @Bean("places")
    public MongoCollection<Document> placeCollection() {
        return mongoDatabase().getCollection("places");
    }

    @Bean("users")
    public MongoCollection<Document> userCollection() {
        return mongoDatabase().getCollection("users");
    }

    @Bean("items")
    public MongoCollection<Document> itemCollection() {
        return mongoDatabase().getCollection("items");
    }

}
