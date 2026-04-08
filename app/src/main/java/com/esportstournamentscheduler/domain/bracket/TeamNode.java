package com.esportstournamentscheduler.domain.bracket;
import com.esportstournamentscheduler.domain.model.Team;

public class TeamNode implements IBracketNode {
    private Team team;

    /**
     * Constructor for TeamNode.
     * @param team The team associated with this node.
     */
    public TeamNode(Team team) {
        this.team = team;
    }

    /**
     * Returns the winner of this node, which is always the team itself for a leaf node.
     * @return The team associated with this node.
     */
    @Override
    public Team getWinner() {
        return team;
    }

    /**
     * A leaf node (team) is always ready.
     * @return true
     */
    @Override
    public boolean isReady() {
        return true; // A leaf node (team) is always ready
    }

    /**
     * Returns the display name of the team associated with this node.
     * @return The team's name.
     */
    @Override
    public String getDisplayName() { 
        return team.getName(); 
    }

}
