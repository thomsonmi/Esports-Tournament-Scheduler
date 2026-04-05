package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament {
    private final String name;
    private final String game;
    private final List<Team> registeredTeams;
    private final List<Match> matches;
    
    private final int MAX_TEAMS;
    private final int MAX_PLAYERS_PER_TEAM;

    private enum TournamentState {
        REGISTRATION,
        IN_PROGRESS,
        COMPLETED
    }

    private Map<String, Team> teamMap; // For quick lookup of teams by ID

    private TournamentState state;

    public Tournament(String name, int maxTeams, int maxPlayersPerTeam, String game) 
    {
        if(maxTeams != 4 && maxTeams != 8) throw new IllegalArgumentException("Max teams must be 4 or 8.");
        if(maxPlayersPerTeam <= 0) throw new IllegalArgumentException("Max players per team must be greater than 0.");
        
        this.MAX_TEAMS = maxTeams;
        this.MAX_PLAYERS_PER_TEAM = maxPlayersPerTeam;
        
        this.name = name;
        this.game = game;
        
        this.registeredTeams = new ArrayList<>();
        this.matches = new ArrayList<>();        
        this.teamMap = new HashMap<>();
        this.state = TournamentState.REGISTRATION; 
    }

    public void startTournament() {

        if(registeredTeams.size() != MAX_TEAMS) throw new IllegalStateException("Tournament must have exactly " + MAX_TEAMS + " teams to start.");

        for(Team team : registeredTeams) {
            teamMap.put(team.getName(), team);
            // Logic to place teams in the bracket

        }

        this.state = TournamentState.IN_PROGRESS;
    }

    public void registerTeam(Team team) {
        if (registeredTeams.size() >= MAX_TEAMS) {
            throw new IllegalStateException("Tournament is full. Maximum " + MAX_TEAMS + " teams allowed.");
        }
        registeredTeams.add(team);
    }
    
    public List<Team> getRegisteredTeams() { 
        return registeredTeams; 
    }

    public List<Match> getMatches() { 
        return matches; 
    }
    
    public String getName() { 
        return name; 
    }

    public String getGame() {
        return game;
    }

    public int getMaxTeams() {
        return MAX_TEAMS;
    }

    public int getMaxPlayersPerTeam() {
        return MAX_PLAYERS_PER_TEAM;
    }

    public TournamentState getState() {
        return state;
    }
}