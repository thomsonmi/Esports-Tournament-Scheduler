package com.esportstournamentscheduler.domain.policy;

import com.esportstournamentscheduler.domain.model.Team;

/**
 * Strategy interface for validating teams before they are registered to a tournament.
 * Different implementations enforce different rules (e.g., flexible vs. strict player counts).
 * Swap the active implementation via {@link com.esportstournamentscheduler.manager.TournamentManager}
 * to change validation behavior at runtime without modifying tournament logic.
 */
public interface ITeamValidationPolicy {

    /**
     * Validates that the team's player count satisfies the policy's requirements.
     * @param team              The team to validate.
     * @param maxPlayersPerTeam The maximum number of players allowed per team.
     * @throws IllegalStateException if the team's roster does not meet policy requirements.
     */
    void validateTeamSize(Team team, int maxPlayersPerTeam);

    /**
     * Validates that the proposed team name does not conflict with any existing team.
     * @param teamName          The name to validate.
     * @param existingTeamNames The set of names already in use.
     * @throws IllegalStateException if the name is already taken.
     */
    void validateUniqueTeamName(String teamName, java.util.Set<String> existingTeamNames);
}
