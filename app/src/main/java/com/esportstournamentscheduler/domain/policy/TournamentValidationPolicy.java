package com.esportstournamentscheduler.domain.policy;

public class TournamentValidationPolicy implements ITournamentValidationPolicy 
{
    @Override
    public void validateNumberOfTeams(int currentTeamCount, int requiredTeamSize) 
    {
        if (currentTeamCount != requiredTeamSize) 
        {
            throw new IllegalStateException("Tournament must have exactly " + requiredTeamSize + " teams.");
        }
    }

    
    
}
