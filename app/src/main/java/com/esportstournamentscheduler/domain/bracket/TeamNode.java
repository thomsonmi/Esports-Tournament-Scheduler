package com.esportstournamentscheduler.domain.bracket;
import com.esportstournamentscheduler.domain.model.Team;

public class TeamNode implements IBracketNode {
    private Team team;

    public TeamNode(Team team) {
        this.team = team;
    }

    @Override
    public Team getWinner() {
        return team;
    }

    @Override
    public boolean isReady() {
        return true; // A leaf node (team) is always ready
    }
    @Override
    public String getDisplayName() { 
        return team.getName(); 
    }

}
