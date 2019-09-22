package com.amairovi.keeper.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private String username;
    private Set<String> places = new HashSet<>();

    public void addPlace(String place){
        places.add(place);
    }
}
