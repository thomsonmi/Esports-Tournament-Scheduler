package com.esportstournamentscheduler.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esportstournamentscheduler.domain.bracket.IBracketNode;
import com.esportstournamentscheduler.domain.bracket.TeamNode;

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

   private final List<List<Match>> bracketRounds = new ArrayList<>();
    
   

    public void startTournament() {
        if(registeredTeams.size() != getMaxTeams()) throw new IllegalStateException("Must have " + getMaxTeams() + " teams.");
        
        Collections.shuffle(registeredTeams);
        bracketRounds.clear();

        // 1. Create the bottom layer (Team Nodes)
        List<IBracketNode> currentNodes = new ArrayList<>();
        for(Team team : registeredTeams) {
            currentNodes.add(new TeamNode(team));
        }

        // 2. Build the tree upward, round by round
        int matchCounter = 1;
        int roundCounter = 1;

        while (currentNodes.size() > 1) {
            List<IBracketNode> nextNodes = new ArrayList<>();
            List<Match> thisRoundMatches = new ArrayList<>();
            
            for (int i = 0; i < currentNodes.size(); i += 2) {
                IBracketNode left = currentNodes.get(i);
                IBracketNode right = currentNodes.get(i + 1);
                
                String mId = "R" + roundCounter + "-M" + matchCounter++;
                Match newMatch = new Match(mId, left, right);
                
                nextNodes.add(newMatch);
                thisRoundMatches.add(newMatch);
            }
            
            bracketRounds.add(thisRoundMatches); // Save the round for printing
            currentNodes = nextNodes; // Move up the tree
            roundCounter++;
        }

        this.state = TournamentState.IN_PROGRESS;
    }

    public List<List<Match>> getBracketRounds() {
        return bracketRounds;
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