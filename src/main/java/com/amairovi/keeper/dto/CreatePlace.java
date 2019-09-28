package com.amairovi.keeper.dto;

import lombok.Data;

@Data
public class CreatePlace {
    private String userId;
    private String placeName;
    private String parentPlaceId;
}
