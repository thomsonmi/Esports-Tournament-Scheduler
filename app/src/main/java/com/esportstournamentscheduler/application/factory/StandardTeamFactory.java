package com.esportstournamentscheduler.application.factory;

import com.esportstournamentscheduler.domain.model.Team;

/**
 * Standard implementation of {@link ITeamFactory}.
 * Creates plain {@link com.esportstournamentscheduler.domain.model.Team} instances
 * with no additional configuration.
 */
public class StandardTeamFactory implements ITeamFactory
{
    /**
     * Creates a new Team with a unique ID and the given name.
     *
     * @param name The name of the team.
     * @return A new Team instance.
     */
    @Override
    public Team createTeam(String name) 
    {
        return new Team(name);
    }    
}
