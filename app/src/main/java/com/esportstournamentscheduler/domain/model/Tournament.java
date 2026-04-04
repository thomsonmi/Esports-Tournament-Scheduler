package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private final String id;
    private final String name;
    private final String game;
    private final List<Team> registeredTeams;
    private List<Match> matches;
    private final int maxPlayersPerTeam;
    
    private static final int MAX_TEAMS = 8;

    public Tournament(String id, String name, String game, int maxPlayersPerTeam) {
        this.id = id;
        this.name = name;
        this.game = game;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.registeredTeams = new ArrayList<>();
        this.matches = new ArrayList<>();
    }

    public void registerTeam(Team team) {
        if (registeredTeams.size() >= MAX_TEAMS) {
            throw new IllegalStateException("Tournament is full. Maximum 8 teams allowed.");
        }
        registeredTeams.add(team);
    }

    public List<Team> getRegisteredTeams() { 
        return registeredTeams; 
    }
    public void setMatches(List<Match> matches) { 
        this.matches = matches; 
    }
    public List<Match> getMatches() { 
        return matches; 
    }
    public String getId() { 
        return id; 
    }
    public String getName() { 
        return name; 
    }
}