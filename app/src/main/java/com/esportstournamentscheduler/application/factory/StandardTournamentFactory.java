package com.esportstournamentscheduler.application.factory;

import com.esportstournamentscheduler.domain.model.Tournament;

public class StandardTournamentFactory implements ITournamentFactory
{
    /**
     * Creates a new Tournament with the given name.
     *
     * @param name The name of the tournament.
     * @param numTeams The number of teams participating in the tournament.
     * @return A new Tournament instance.
     */
    @Override
    public Tournament CreateTournament(String name, int numTeams) 
    {
        int defaultMaxPlayersPerTeam = 4; // No specific team size requirement
        String gameName = ""; // No specific game associated with this tournament
        return new Tournament(name, numTeams, defaultMaxPlayersPerTeam, gameName); // Standard tournament with default values
    }
    
}
