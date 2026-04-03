package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tournament {
    private final String name;
    private final List<Team> registeredTeams;
    private List<Match> matches;
    
    private final int MAX_TEAMS;
    private final int MAX_PLAYERS_PER_TEAM;

    private Team[] bracket;

    private enum TournamentState {
        REGISTRATION,
        IN_PROGRESS,
        COMPLETED
    }

    private HashMap<String, Team> teamMap; // For quick lookup of teams by ID

    private TournamentState state;

    public Tournament(String name, int maxTeams, int maxPlayersPerTeam) 
    {
        if(maxTeams != 4 && maxTeams != 8) throw new IllegalArgumentException("Max teams must be 4 or 8.");
        if(maxPlayersPerTeam != 8) throw new IllegalArgumentException("Max players per team must be 8.");

        this.name = name;
        this.registeredTeams = new ArrayList<>();
        this.matches = new ArrayList<>();        
        this.MAX_TEAMS = maxTeams;
        this.MAX_PLAYERS_PER_TEAM = maxPlayersPerTeam;
        this.teamMap = new HashMap<>();
        this.bracket = new Team[2*maxTeams - 1]; // For a single-elimination tournament with maxTeams teams
        this.state = TournamentState.REGISTRATION; 
    }

    public void startTournament() {

        if(registeredTeams.size() != MAX_TEAMS) throw new IllegalStateException("Tournament must have exactly " + MAX_TEAMS + " teams to start.");

        for(Team team : registeredTeams) {
            teamMap.put(team.getId(), team);
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
    public TournamentState getState() {
        return state;
    }
}