package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a team competing in the esports tournament.
 * A team has a name and an ordered roster of players.
 */
public class Team {
    /** The team's unique display name. */
    private final String name;

    /** The ordered roster of players belonging to this team. */
    private List<Player> players = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the team.
     * @return String - The team's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a player to the team's roster.
     * @param player The player to add.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Returns the list of players in the team.
     * @return List<Player> The players in the team.
     */
    public List<Player> getPlayers() {
        return players;
    }


    /**
     * Returns a string representation of the team, including its name and players.
     * @return String - A string representation of the team.
     */
    @Override
    public String toString() {
        List<String> playerNames = new ArrayList<>();
        for (Player p : players) {
            playerNames.add(p.getName());
        }
        
        // Format the output: TeamName (X players: Name1, Name2, Name3)
        return name + " (" + players.size() + " players: " + String.join(", ", playerNames) + ")";
    }
}