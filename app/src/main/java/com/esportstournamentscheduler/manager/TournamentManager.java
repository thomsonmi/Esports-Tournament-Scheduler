package com.esportstournamentscheduler.manager;

import java.util.HashMap;
import java.util.Map;

import com.esportstournamentscheduler.domain.model.Team;
import com.esportstournamentscheduler.domain.model.Tournament;

public class TournamentManager 
{
    private Tournament tournament;
    private Map<String, Team> teams = new HashMap<>();

    public TournamentManager() {
         tournament = new Tournament(java.util.UUID.randomUUID().toString(), "Default Tournament", 8, 8);

    }

    public void CreateTeam(String teamName) {
        
        Team newTeam = new Team(teamName);
        if(teams.containsKey(newTeam.getId())) throw new IllegalArgumentException("Team ID already exists.");

        teams.put(newTeam.getId(), newTeam);

    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void clearTeams() {
        teams.clear();
    }

    public void removeTeam(String teamId) {
        if(!teams.containsKey(teamId)) throw new IllegalArgumentException("Team ID does not exist.");
        teams.remove(teamId);
    }

    public void registerTeam


}
