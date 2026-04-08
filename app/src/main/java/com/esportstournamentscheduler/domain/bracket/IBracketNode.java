package com.esportstournamentscheduler.domain.bracket;
import com.esportstournamentscheduler.domain.model.Team;

public interface IBracketNode {
    Team getWinner();
    boolean isReady();

    String getDisplayName();
}
