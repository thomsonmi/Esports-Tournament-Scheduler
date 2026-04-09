package com.esportstournamentscheduler.domain.policy;

/**
 * Default implementation of {@link ITournamentValidationPolicy}.
 * Enforces that a tournament has exactly the required number of teams before it can start.
 */
public class TournamentValidationPolicy implements ITournamentValidationPolicy 
{
    /**
     * Validates that {@code currentTeamCount} equals {@code requiredTeamSize}.
     * @param currentTeamCount The number of teams currently registered.
     * @param requiredTeamSize The exact number of teams required to start.
     * @throws IllegalStateException if the counts do not match.
     */
    @Override
    public void validateNumberOfTeams(int currentTeamCount, int requiredTeamSize) 
    {
        if (currentTeamCount != requiredTeamSize) 
        {
            throw new IllegalStateException("Tournament must have exactly " + requiredTeamSize + " teams.");
        }
    }

    
    
}
