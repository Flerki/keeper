package com.amairovi.keeper.model;

import lombok.Data;
import lombok.ToString;

import java.util.*;

@Data
@ToString
public class User {
    private String id;
    private String email;
    private String password;
    private Set<String> places = new HashSet<>();
    private List<String> recentItems = new ArrayList<>();

    public void addPlace(String place){
        places.add(place);
    }
}
