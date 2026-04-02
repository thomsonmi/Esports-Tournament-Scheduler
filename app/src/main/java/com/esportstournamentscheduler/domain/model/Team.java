package com.esportstournamentscheduler.domain.model;

public class Team {
    private final String id;
    private String name;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}