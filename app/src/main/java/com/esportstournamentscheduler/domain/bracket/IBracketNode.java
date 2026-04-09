package com.esportstournamentscheduler.domain.bracket;
import com.esportstournamentscheduler.domain.model.Team;

/**
 * Composite pattern component for the tournament bracket tree.
 * Both leaf nodes (teams, represented by {@link TeamNode}) and inner nodes
 * (matches, represented by {@link com.esportstournamentscheduler.domain.model.Match})
 * implement this interface, allowing the bracket renderer to traverse
 * the entire tree uniformly.
 */
public interface IBracketNode {

    /**
     * Returns the winning team for this bracket position, or {@code null}
     * if the outcome has not yet been determined.
     * @return The winning {@link com.esportstournamentscheduler.domain.model.Team}, or null.
     */
    Team getWinner();

    /**
     * Returns true when this node's participants are fully determined and
     * the node (or match) is eligible to proceed.
     * @return true if this node is ready to be played or has already been resolved.
     */
    boolean isReady();

    /**
     * Returns a short human-readable label for this bracket position.
     * Used by the ASCII bracket renderer to label slots in the tree.
     * @return The team name if resolved, otherwise a placeholder string.
     */
    String getDisplayName();
}
