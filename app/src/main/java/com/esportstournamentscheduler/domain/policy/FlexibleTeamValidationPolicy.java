package com.esportstournamentscheduler.domain.policy;

import java.util.Set;

import com.esportstournamentscheduler.domain.model.Team;

public class FlexibleTeamValidationPolicy implements ITeamValidationPolicy 
{

    @Override
    public void validateTeam(Team team, int maxPlayersPerTeam) 
    {
        if (team.getPlayers().isEmpty()) 
        {
            throw new IllegalStateException("Flexible Policy: Team '" + team.getName() + "' must have at least 1 player.");
        }
        if (team.getPlayers().size() > maxPlayersPerTeam) 
        {
            throw new IllegalStateException("Flexible Policy: Team '" + team.getName() + 
                "' cannot exceed " + maxPlayersPerTeam + " players.");
        }
    }

    @Override
    public void validateUniqueTeamName(String teamName, Set<String> existingTeamNames) {
        if (existingTeamNames.contains(teamName)) {
            throw new IllegalStateException("Flexible Policy: Team name '" + teamName + "' already exists.");
        }
    }
    
}
