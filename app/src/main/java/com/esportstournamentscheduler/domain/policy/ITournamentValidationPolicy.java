package com.esportstournamentscheduler.domain.policy;

/**
 * Strategy interface for validating tournament-level constraints before a tournament starts.
 * Implementations define rules such as required team counts.
 */
public interface ITournamentValidationPolicy 
{
    /**
     * Validates that the number of registered teams satisfies the tournament's requirement.
     * @param currentTeamCount The number of teams currently registered.
     * @param requiredTeamSize The exact number of teams the tournament requires.
     * @throws IllegalStateException if the counts do not match.
     */
    void validateNumberOfTeams(int currentTeamCount, int requiredTeamSize);
}
