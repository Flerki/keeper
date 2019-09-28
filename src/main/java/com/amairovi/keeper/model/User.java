package com.amairovi.keeper.model;

import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString
public class User {
    private String id;
    private String email;
    private Set<String> places = new HashSet<>();

    public void addPlace(String place){
        places.add(place);
    }
}
