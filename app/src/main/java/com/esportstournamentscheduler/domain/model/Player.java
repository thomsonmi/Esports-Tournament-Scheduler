package com.esportstournamentscheduler.domain.model;

public class Player {

    private final String id;
    private final String name;

    public Player(String name) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() { 
        return id; 
    }
    public String getName() { 
        return name; 
    }
    
}
