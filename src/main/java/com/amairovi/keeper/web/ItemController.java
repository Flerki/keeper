package com.amairovi.keeper.web;

import com.amairovi.keeper.dto.CreateItem;
import com.amairovi.keeper.dto.UpdateItem;
import com.amairovi.keeper.model.Item;
import com.amairovi.keeper.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public String create(@RequestBody CreateItem request) {

        String itemName = request.getItemName();
        String placeId = request.getPlaceId();

        Item item = itemService.create(itemName, placeId);
        return item.getId();
    }

    @PutMapping("/{itemId}")
    public void update(@PathVariable String itemId, @RequestBody UpdateItem request) {
        log.debug("Update item {}: {}.", itemId, request);

        String name = request.getItemName();
        String parentItemId = request.getPlaceId();

        itemService.update(itemId, name, parentItemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable String itemId) {
        log.debug("Delete item {}.", itemId);

        itemService.delete(itemId);
    }
}
