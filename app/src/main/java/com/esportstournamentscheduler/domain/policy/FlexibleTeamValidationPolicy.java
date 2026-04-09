package com.esportstournamentscheduler.domain.policy;

import java.util.Set;

import com.esportstournamentscheduler.domain.model.Team;

/**
 * Flexible implementation of {@link ITeamValidationPolicy}.
 * Accepts any team with at least 1 player, up to the configured maximum.
 * Used as the default policy when strict roster requirements are not needed.
 */
public class FlexibleTeamValidationPolicy implements ITeamValidationPolicy 
{

    /**
     * Validates that the team has between 1 and {@code maxPlayersPerTeam} players (inclusive).
     * @param team              The team to validate.
     * @param maxPlayersPerTeam The upper bound on roster size.
     * @throws IllegalStateException if the team has 0 players or exceeds the maximum.
     */
    @Override
    public void validateTeamSize(Team team, int maxPlayersPerTeam) 
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

    /**
     * Validates that the team name is not already in use.
     * @param teamName          The proposed name.
     * @param existingTeamNames The set of currently registered team names.
     * @throws IllegalStateException if the name is already taken.
     */
    @Override
    public void validateUniqueTeamName(String teamName, Set<String> existingTeamNames) {
        if (existingTeamNames.contains(teamName)) {
            throw new IllegalStateException("Flexible Policy: Team name '" + teamName + "' already exists.");
        }
    }
}
