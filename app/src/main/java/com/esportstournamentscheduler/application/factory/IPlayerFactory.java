package com.esportstournamentscheduler.application.factory;
import com.esportstournamentscheduler.domain.model.Player;

public interface IPlayerFactory { 

    Player createPlayer(String name);
    
}
