package com.esportstournamentscheduler.application.factory;

import com.esportstournamentscheduler.domain.model.Tournament;

public class CounterStrikeTournamentFactory implements ITournamentFactory
{
    /**
     * Creates a new Counter-Strike tournament with the given name.
     *
     * @param name The name of the tournament.
     * @return A new Tournament instance configured for Counter-Strike.
     */
    @Override
    public Tournament CreateTournament(String name, int numTeams) 
    {
        String game = "Counter-Strike: Global Offensive";
        int maxPlayersPerTeam = 5; // Counter-Strike teams have 5 players
        
        Tournament tournament = new Tournament(name, numTeams, maxPlayersPerTeam, game);

        return tournament;
    }
    
}
