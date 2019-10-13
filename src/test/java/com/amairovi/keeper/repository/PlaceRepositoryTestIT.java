package com.amairovi.keeper.repository;

import com.amairovi.keeper.model.Place;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaceRepositoryTestIT {
    private static final String DATABASE_NAME = "embedded";

    private static MongodExecutable MONGO_EXECUTABLE;
    private static MongodProcess MONGOD;
    private static MongoCollection<Document> PLACES;
    private PlaceRepository placeRepository;

    @BeforeAll
    public static void beforeAll() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        String bindIp = "localhost";
        int port = 12345;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build();
        MONGO_EXECUTABLE = starter.prepare(mongodConfig);
        MONGOD = MONGO_EXECUTABLE.start();
        MongoClient mongo = MongoClients.create(String.format("mongodb://%s:%d", bindIp, port));
        MongoDatabase database = mongo.getDatabase(DATABASE_NAME);
        PLACES = database.getCollection("places");

    }

    @BeforeEach
    public void beforeEach() {
        PLACES.drop();
        placeRepository = new PlaceRepository(PLACES);
    }

    @AfterAll
    public static void afterAll() {
        if (MONGOD != null) {
            MONGOD.stop();
            MONGO_EXECUTABLE.stop();
        }
    }

    @Test
    void when_save_then_return_save_to_db_and_return_generated_id() {
        Place place = new Place();
        String expectedName = "name example";
        place.setName(expectedName);
        String expectedParentId = "parent's id example";
        place.setParentId(expectedParentId);
        String id = placeRepository.save(place);

        List<Document> actual = PLACES.find().into(new ArrayList<>());

        assertThat(actual).hasSize(1);
        System.out.println(id);

        Document actualPlace = actual.get(0);
        assertThat(actualPlace.get("_id")).isEqualTo(new ObjectId(id));
        assertThat(actualPlace.get("name")).isEqualTo(expectedName);
        assertThat(actualPlace.get("parentId")).isEqualTo(expectedParentId);
    }

    @Test
    void when_update_not_existed_then_nothing_happens() {
        Place place = new Place();
        place.setId("5da2f1680fce18315c020d5e");
        placeRepository.update(place);

        List<Document> actual = PLACES.find().into(new ArrayList<>());

        assertThat(actual).isEmpty();
    }

    @Test
    void when_update_existed_then_update_it() {
        ObjectId id = new ObjectId();
        Document initial = new Document()
                .append("_id", id)
                .append("name", "name example")
                .append("parentId", "parent's id example");
        PLACES.insertOne(initial);

        Place place = new Place();
        place.setId(id.toString());
        String expectedName = "new name example";
        place.setName(expectedName);
        String expectedParentId = "new parent's id example";
        place.setParentId(expectedParentId);

        placeRepository.update(place);

        List<Document> actual = PLACES.find().into(new ArrayList<>());

        assertThat(actual).hasSize(1);
        System.out.println(id);

        Document actualPlace = actual.get(0);
        assertThat(actualPlace.get("_id")).isEqualTo(id);
        assertThat(actualPlace.get("name")).isEqualTo(expectedName);
        assertThat(actualPlace.get("parentId")).isEqualTo(expectedParentId);
    }

    @Test
    void when_find_by_id_non_existing_then_return_empty(){
        String id = new ObjectId().toString();
        Optional<Place> place = placeRepository.findById(id);

        assertThat(place).isEmpty();
    }

    @Test
    void when_find_by_id_existing_then_return_it(){
        ObjectId id = new ObjectId();
        PLACES.insertOne(new Document("_id", id));

        Optional<Place> actual = placeRepository.findById(id.toString());
        assertThat(actual).isNotEmpty();

        ObjectId anotherId = new ObjectId();
        PLACES.insertOne(new Document("_id", anotherId));
        actual = placeRepository.findById(anotherId.toString());
        assertThat(actual).isNotEmpty();
    }

    @Test
    void when_delete_non_existing_then_nothing_happens(){
        placeRepository.delete(new ObjectId().toString());

        List<Document> actual = PLACES.find().into(new ArrayList<>());

        assertThat(actual).isEmpty();
    }

    @Test
    void when_delete_existing_then_delete_it(){
        ObjectId id = new ObjectId();
        PLACES.insertOne(new Document("_id", id));
        PLACES.insertOne(new Document("_id", new ObjectId()));
        PLACES.insertOne(new Document("_id", new ObjectId()));

        placeRepository.delete(id.toString());

        List<Document> actual = PLACES.find().into(new ArrayList<>());

        assertThat(actual).hasSize(2);
        Predicate<Document> notDeleted = d -> !d.getObjectId("_id").equals(id);
        assertThat(actual).allMatch(notDeleted);
    }
}