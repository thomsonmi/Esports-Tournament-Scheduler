package com.esportstournamentscheduler.domain.policy;

import com.esportstournamentscheduler.domain.model.Team;

/**
 * Strict implementation of {@link ITeamValidationPolicy}.
 * Requires a team's roster to equal exactly the tournament's maximum player count.
 * Use this policy when all teams must have a full, identical roster size.
 */
public class StrictTeamValidationPolicy implements ITeamValidationPolicy 
{
    /**
     * Validates that the team's roster size equals exactly {@code maxPlayersPerTeam}.
     * @param team              The team to validate.
     * @param maxPlayersPerTeam The exact player count required.
     * @throws IllegalStateException if the team does not have exactly the required number of players.
     */
    @Override
    public void validateTeamSize(Team team, int maxPlayersPerTeam) {
        if (team.getPlayers().size() != maxPlayersPerTeam) {
            throw new IllegalStateException("Team does not have the required number of players");
        }
    }

    /**
     * Validates that the team name is not already in use.
     * @param teamName          The proposed name.
     * @param existingTeamNames The set of currently registered team names.
     * @throws IllegalStateException if the name is already taken.
     */
    @Override
    public void validateUniqueTeamName(String teamName, java.util.Set<String> existingTeamNames) 
    {        
        if (existingTeamNames.contains(teamName)) {
            throw new IllegalStateException("Team name '" + teamName + "' already exists.");
        }
    }
    
}
