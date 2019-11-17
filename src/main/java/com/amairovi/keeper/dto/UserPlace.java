package com.amairovi.keeper.dto;

import lombok.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value
public class UserPlace {
    private final String id;
    private final String name;
    private final List<UserPlace> children;
    private final String parentId;

    public Set<String> generateHierarchyIds() {
        HashSet<String> ids = new HashSet<>();

        children.stream()
                .map(UserPlace::generateHierarchyIds)
                .forEach(ids::addAll);

        ids.add(id);
        return ids;
    }

    public Set<UserPlace> generateHierarchy() {
        HashSet<UserPlace> hierarchy = new HashSet<>();

        children.stream()
                .map(UserPlace::generateHierarchy)
                .forEach(hierarchy::addAll);

        hierarchy.add(this);
        return hierarchy;
    }
}
