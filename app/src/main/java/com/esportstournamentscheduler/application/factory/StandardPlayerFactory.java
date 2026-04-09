package com.esportstournamentscheduler.application.factory;

import com.esportstournamentscheduler.domain.model.Player;

/**
 * Standard implementation of {@link IPlayerFactory}.
 * Creates plain {@link com.esportstournamentscheduler.domain.model.Player} instances
 * with an auto-generated UUID and the provided display name.
 */
public class StandardPlayerFactory implements IPlayerFactory
{
    /**
     * Creates a new Player with a unique ID and the given name.
     *
     * @param name The name of the player.
     * @return A new Player instance.
     */
    @Override
    public Player createPlayer(String name) 
    {
        return new Player(name);
    }    
}
