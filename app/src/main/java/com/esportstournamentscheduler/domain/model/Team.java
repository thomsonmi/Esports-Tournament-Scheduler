package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final String name;
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
     * Adds a player to the team.
     * @param player void - The player to add.
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