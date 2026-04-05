package com.esportstournamentscheduler.domain.model;

import java.util.List;

public class Team {
    private final String name;
    private List<Player> players;

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}