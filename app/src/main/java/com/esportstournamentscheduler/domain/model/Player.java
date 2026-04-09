package com.esportstournamentscheduler.domain.model;

/**
 * Represents a player in the esports tournament system.
 * Each player is assigned a globally unique ID on creation.
 */
public class Player {

    /** Globally unique identifier generated at construction time. */
    private final String id;

    /** Display name shown in brackets and team rosters. */
    private final String name;

    /**
     * Constructs a new Player with a randomly generated UUID and the given display name.
     * @param name The player's display name.
     */
    public Player(String name) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
    }

    /**
     * Returns the player's unique identifier.
     * The ID is a UUID string assigned at construction and never changes.
     * @return The player's UUID string.
     */
    public String getId() { 
        return id; 
    }
    public String getName() { 
        return name; 
    }
    
}
