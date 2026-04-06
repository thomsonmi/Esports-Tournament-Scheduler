package com.esportstournamentscheduler.domain.model;
import com.esportstournamentscheduler.domain.policy.ITeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.StrictTeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.FlexibleTeamValidationPolicy; 
import com.esportstournamentscheduler.domain.policy.ITournamentValidationPolicy;
import com.esportstournamentscheduler.domain.policy.TournamentValidationPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament {
    private final String name;
    private final String game;
    private final List<Team> registeredTeams;
    private final List<Match> matches;
    private Map<String, Team> teamMap; // For quick lookup of teams by ID
    
    private final int REQUIRED_TEAM_SIZE;
    private final int MAX_PLAYERS_PER_TEAM;

    private ITeamValidationPolicy teamValidationPolicy;
    private ITournamentValidationPolicy tournamentValidationPolicy;

    private enum TournamentState {
        REGISTRATION,
        IN_PROGRESS,
        COMPLETED
    }
    private TournamentState state;

    public Tournament(String name, int maxTeams, int maxPlayersPerTeam, String game) 
    {
        if(maxTeams != 4 && maxTeams != 8) throw new IllegalArgumentException("Max teams must be 4 or 8.");
        if(maxPlayersPerTeam <= 0) throw new IllegalArgumentException("Max players per team must be greater than 0.");
        
        this.REQUIRED_TEAM_SIZE = maxTeams;
        this.MAX_PLAYERS_PER_TEAM = maxPlayersPerTeam;
        
        this.name = name;
        this.game = game;

        this.teamValidationPolicy = new StrictTeamValidationPolicy(); // Default to strict policy, can be changed later
        this.tournamentValidationPolicy = new TournamentValidationPolicy(); // Default tournament validation policy
        
        this.registeredTeams = new ArrayList<>();
        this.matches = new ArrayList<>();        
        this.teamMap = new HashMap<>();
        this.state = TournamentState.REGISTRATION; 
    }

    public void startTournament() {

        tournamentValidationPolicy.validateNumberOfTeams(registeredTeams.size(), REQUIRED_TEAM_SIZE);

        CreateBracket();

        this.state = TournamentState.IN_PROGRESS;

    }

    public void registerTeam(Team team) {
        
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to register teams.");
        tournamentValidationPolicy.validateNumberOfTeams(registeredTeams.size() + 1, REQUIRED_TEAM_SIZE);
        teamValidationPolicy.validateTeam(team, MAX_PLAYERS_PER_TEAM);
        teamValidationPolicy.validateUniqueTeamName(team.getName(), teamMap.keySet());
        registeredTeams.add(team);
    }

    public void CreateBracket() {
        if (state != TournamentState.REGISTRATION) 
        {
             throw new IllegalStateException("Tournament must be in registration phase to create bracket.");
        }

        if(registeredTeams.size() != REQUIRED_TEAM_SIZE) 
            throw new IllegalStateException("Tournament must have exactly " + REQUIRED_TEAM_SIZE + " teams to create bracket.");


        // Logic to create matches based on registered teams


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
        return REQUIRED_TEAM_SIZE;
    }

    public int getMaxPlayersPerTeam() {
        return MAX_PLAYERS_PER_TEAM;
    }

    public boolean isRegistrationOpen() {
        return state == TournamentState.REGISTRATION;
    }

    public boolean isInProgress() {
        return state == TournamentState.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return state == TournamentState.COMPLETED;
    }

    public void removeTeam(String teamName) {
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to remove teams.");
        if(!teamMap.containsKey(teamName)) throw new IllegalArgumentException("Team name does not exist.");
        Team teamToRemove = teamMap.get(teamName);
        registeredTeams.remove(teamToRemove);
        teamMap.remove(teamName);
    }

     public void clearTeams() {
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to clear teams.");
        registeredTeams.clear();
        teamMap.clear();
    }
}