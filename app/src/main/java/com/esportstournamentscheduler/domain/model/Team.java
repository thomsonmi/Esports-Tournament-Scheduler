package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final String name;
    private List<Player> players = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void addPlayer(Player player) {
        players.add(player);
    }
    public List<Player> getPlayers() {
        return players;
    }


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