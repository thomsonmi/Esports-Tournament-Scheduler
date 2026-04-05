package com.esportstournamentscheduler.manager;

import java.util.HashMap;
import java.util.Map;

import com.esportstournamentscheduler.application.factory.ITeamFactory;
import com.esportstournamentscheduler.application.factory.IPlayerFactory;
import com.esportstournamentscheduler.domain.model.Player;
import com.esportstournamentscheduler.domain.model.Team;
import com.esportstournamentscheduler.domain.model.Tournament;

public class TournamentManager 
{
    private Tournament tournament;
    private Map<String, Team> teams = new HashMap<>();

    private final ITeamFactory teamFactory;
    private final IPlayerFactory playerFactory;

    public TournamentManager(ITeamFactory teamFactory, IPlayerFactory playerFactory) {
        this.teamFactory = teamFactory;
        this.playerFactory = playerFactory;
        tournament = new Tournament("Default Tournament", 8,5, "Default Game");

    }

    public void CreateTeam(String teamName) {
        
        Team newTeam = teamFactory.createTeam(teamName);
        if(teams.containsKey(newTeam.getName())) throw new IllegalArgumentException("Team name already exists.");

        teams.put(newTeam.getName(), newTeam);

    }
    public void addPlayerToTeam(String teamName, String playerName) {
        Team team = teams.get(teamName);
        
        
        if (team == null) {
            throw new IllegalArgumentException("Team '" + teamName + "' does not exist.");
        }
        
        
        if (team.getPlayers().size() >= tournament.getMaxPlayersPerTeam()) {
            throw new IllegalStateException("Team '" + teamName + "' is full. Maximum " + tournament.getMaxPlayersPerTeam() + " players allowed.");
        }

        // Use the factory to create the player, then add them to the team
        Player newPlayer = playerFactory.createPlayer(playerName);
        team.addPlayer(newPlayer);
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
