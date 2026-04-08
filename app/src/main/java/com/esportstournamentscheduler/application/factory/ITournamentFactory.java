package com.esportstournamentscheduler.application.factory;

import com.esportstournamentscheduler.domain.model.Tournament;

public interface ITournamentFactory {
    Tournament CreateTournament(String name, int numTeams);
    
}
