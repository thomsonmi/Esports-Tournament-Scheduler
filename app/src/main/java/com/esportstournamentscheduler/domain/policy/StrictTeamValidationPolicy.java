package com.esportstournamentscheduler.domain.policy;

import com.esportstournamentscheduler.domain.model.Team;

public class StrictTeamValidationPolicy implements ITeamValidationPolicy 
{
    @Override
    public void validateTeamSize(Team team, int maxPlayersPerTeam) {
        if (team.getPlayers().size() != maxPlayersPerTeam) {
            throw new IllegalStateException("Team does not have the required number of players");
        }
    }

    @Override
    public void validateUniqueTeamName(String teamName, java.util.Set<String> existingTeamNames) 
    {        
        if (existingTeamNames.contains(teamName)) {
            throw new IllegalStateException("Team name '" + teamName + "' already exists.");
        }
    }
    
}
