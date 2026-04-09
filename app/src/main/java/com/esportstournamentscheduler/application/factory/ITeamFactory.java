package com.esportstournamentscheduler.application.factory;
import com.esportstournamentscheduler.domain.model.Team;

/**
 * Factory interface for creating {@link com.esportstournamentscheduler.domain.model.Team} instances.
 * Decouples team construction from the rest of the application so that the
 * creation strategy can be swapped without touching business logic.
 */
public interface ITeamFactory {

    /**
     * Creates and returns a new Team with the given name.
     * @param name The display name for the team.
     * @return A new {@link com.esportstournamentscheduler.domain.model.Team} instance.
     */
    Team createTeam(String name);
    
}
