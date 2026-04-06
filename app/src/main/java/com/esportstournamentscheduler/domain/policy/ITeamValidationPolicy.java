package com.esportstournamentscheduler.domain.policy;

import com.esportstournamentscheduler.domain.model.Team;

public interface ITeamValidationPolicy {
    void validateTeam(Team team, int maxPlayersPerTeam);
    void validateUniqueTeamName(String teamName, java.util.Set<String> existingTeamNames);
}
