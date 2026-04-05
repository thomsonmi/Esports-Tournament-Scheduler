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
        tournament = new Tournament("Default Tournament", 8, 4, "Game");

    }

    public void CreateTeam(String teamName) {
        
        Team newTeam = new Team(teamName);
        if(teams.containsKey(newTeam.getName())) throw new IllegalArgumentException("Team name already exists.");

        teams.put(newTeam.getName(), newTeam);

    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void clearTeams() {
        teams.clear();
    }

    public void removeTeam(String teamName) {
        if(!teams.containsKey(teamName)) throw new IllegalArgumentException("Team name does not exist.");
        teams.remove(teamName);
    }
}
