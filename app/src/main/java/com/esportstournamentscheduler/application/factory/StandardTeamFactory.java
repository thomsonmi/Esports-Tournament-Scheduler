package com.esportstournamentscheduler.application.factory;

import com.esportstournamentscheduler.domain.model.Team;

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
