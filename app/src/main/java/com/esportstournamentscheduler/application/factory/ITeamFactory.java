package com.esportstournamentscheduler.application.factory;
import com.esportstournamentscheduler.domain.model.Team;

public interface ITeamFactory {

    Team createTeam(String name);
    
}
