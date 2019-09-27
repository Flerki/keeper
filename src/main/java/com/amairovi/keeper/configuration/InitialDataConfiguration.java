package com.amairovi.keeper.configuration;

import com.amairovi.keeper.model.Place;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generate-data", havingValue = "true")
@Slf4j
public class InitialDataConfiguration {

    private final MongoConfiguration mongoConfiguration;

    @PostConstruct
    public void init() {
        log.info("Generate and upload some data to db");

        addPlaces();
    }

    private void addPlaces() {
        log.info("Begin to generate and upload some places to db");

        Resource resource = new ClassPathResource("data/places.json");
        try(InputStream input = resource.getInputStream()){
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String json = reader.lines().collect(Collectors.joining());
            ObjectMapper objectMapper = new ObjectMapper();
            Place[] places = objectMapper.readValue(json, Place[].class);

            MongoCollection<Document> placeCollection = mongoConfiguration.getPlaceCollection();
            placeCollection.drop();

            Arrays.stream(places)
                    .forEach(p -> {

                        Document document = new Document("name", p.getName())
                                .append("_id", new ObjectId(p.getId()))
                                .append("parentId", p.getParentId());

                        placeCollection.insertOne(document);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("End to generate and upload some places to db");

    }
}
