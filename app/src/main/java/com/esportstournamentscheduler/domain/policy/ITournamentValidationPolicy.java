package com.esportstournamentscheduler.domain.policy;

public interface ITournamentValidationPolicy 
{
    void validateNumberOfTeams(int currentTeamCount, int requiredTeamSize);
}
