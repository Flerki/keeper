package com.amairovi.keeper.web;


import com.amairovi.keeper.dto.CreatePlace;
import com.amairovi.keeper.model.Place;
import com.amairovi.keeper.model.User;
import com.amairovi.keeper.service.PlaceService;
import com.amairovi.keeper.service.UserPlaceService;
import com.amairovi.keeper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final UserService userService;
    private final UserPlaceService userPlaceService;

    @PostMapping
    public String create(@RequestBody CreatePlace request) {

        String placeName = request.getPlaceName();
        String parentPlaceId = request.getParentPlaceId();

        Place place = placeService.create(placeName, parentPlaceId);
        User user = userService.findById(request.getUserId());

        userPlaceService.save(place, user);

        return place.getId();
    }
}
