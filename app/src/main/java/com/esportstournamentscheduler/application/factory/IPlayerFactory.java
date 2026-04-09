package com.esportstournamentscheduler.application.factory;
import com.esportstournamentscheduler.domain.model.Player;

/**
 * Factory interface for creating {@link com.esportstournamentscheduler.domain.model.Player} instances.
 * Decouples player construction from the rest of the application so that the
 * creation strategy can be swapped without touching business logic.
 */
public interface IPlayerFactory { 

    /**
     * Creates and returns a new Player with the given display name.
     * @param name The display name for the player.
     * @return A new {@link com.esportstournamentscheduler.domain.model.Player} instance.
     */
    Player createPlayer(String name);
    
}
