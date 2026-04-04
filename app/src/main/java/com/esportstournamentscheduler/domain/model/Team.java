package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final String id;
    private String name;
    private List<Player> players;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
        this.players = new ArrayList<>();
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