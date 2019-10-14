package com.amairovi.keeper.repository;

import com.amairovi.keeper.model.Item;
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

import static com.mongodb.client.model.Filters.eq;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    @Qualifier("items")
    private final MongoCollection<Document> items;

    public String save(Item item) {
        Document document = new Document()
                .append("name", item.getName())
                .append("placeId", item.getPlaceId());

        items.insertOne(document);

        return document.get("_id").toString();
    }

    public Optional<Item> findById(String id) {
        FindIterable<Document> documents = items.find(eq("_id", new ObjectId(id)));

        return Optional.ofNullable(documents.first())
                .map(this::documentToItem);
    }

    public List<Item> findByPlaceId(String id) {
        return items.find(eq("placeId", id))
                .map(this::documentToItem)
                .into(new ArrayList<>());
    }

    public void update(Item item) {
        Document document = new Document()
                .append("name", item.getName())
                .append("placeId", item.getPlaceId());

        ObjectId id = new ObjectId(item.getId());
        items.replaceOne(eq("_id", id), document);
    }

    public void delete(String id) {
        items.deleteOne(eq("_id", new ObjectId(id)));
    }


    private Item documentToItem(Document d) {
        Item item = new Item();
        item.setId(d.getObjectId("_id").toString());
        item.setName(d.getString("name"));
        item.setPlaceId(d.getString("placeId"));
        return item;
    }
}
