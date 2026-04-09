package com.esportstournamentscheduler.domain.model;

import com.esportstournamentscheduler.domain.bracket.IBracketNode; // Make sure this import matches your folder structure!

/**
 * Composite node in the tournament bracket tree representing a scheduled match.
 * A Match holds two child {@link IBracketNode}s (either {@code TeamNode} leaves or
 * nested Matches) and tracks the result once scores are recorded.
 * Implements the Composite pattern — both leaf nodes (teams) and inner nodes
 * (matches) share the same {@link IBracketNode} interface.
 */
public class Match implements IBracketNode {
    private final String matchId;
    
    // The Composite Tree Nodes
    private final IBracketNode leftChild;
    private final IBracketNode rightChild;
    
    /** The winner of this match; null until {@link #finalizeScores} is called. */
    private Team winner;

    /**
     * Lifecycle states of a match from creation through completion.
     * A match starts PENDING, transitions to IN_PROGRESS when both participants
     * are known, and moves to COMPLETED once scores are finalized.
     */
    public enum MatchState { PENDING, IN_PROGRESS, COMPLETED }
    private MatchState state;

    /**
     * Constructs a new pending Match between two bracket nodes.
     * @param matchId   Human-readable identifier (e.g., "Match 1") used for display and lookup.
     * @param leftChild  The left bracket node (a team or the winner of a prior match).
     * @param rightChild The right bracket node (a team or the winner of a prior match).
     */
    // Constructor
    public Match(String matchId, IBracketNode leftChild, IBracketNode rightChild) {
        this.matchId = matchId;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.state = MatchState.PENDING;
    }

    // --- Getters for the bracket printer ---

    /** Returns the left child node; used by the bracket renderer to trace the tree. */
    public IBracketNode getLeftNode() { 
        return leftChild; 
    }

    /** Returns the right child node; used by the bracket renderer to trace the tree. */
    public IBracketNode getRightNode() { 
        return rightChild; 
    }

    @Override
    public Team getWinner() { 
        return winner; 
    }

    /**
     * Returns true when both child nodes have a determined winner, meaning
     * this match's participants are fully known and it can be started.
     * @return true if both children have a non-null winner.
     */
    @Override
    public boolean isReady() {
        return leftChild.getWinner() != null && rightChild.getWinner() != null;
    }

    /**
     * Returns the display name for this bracket node.
     * Shows the winner's name if the match is completed, or a placeholder indicating
     * which match this slot depends on.
     * @return The winner's team name, or "Winner of &lt;matchId&gt;" if not yet complete.
     */
    @Override
    public String getDisplayName() {
        if (state == MatchState.COMPLETED && winner != null) {
            return winner.getName();
        }
        return "Winner of " + matchId;
    }

    // --- Core Logic ---

    /**
     * Transitions this match from PENDING to IN_PROGRESS.
     * @throws IllegalStateException if the match is not currently PENDING.
     * @throws IllegalStateException if both participants are not yet determined.
     */
    public void startMatch() {
        if (state != MatchState.PENDING) {
            throw new IllegalStateException("Match cannot be started. Current state: " + state);
        }
        
        if (!isReady()) {
            throw new IllegalStateException("Match cannot start. Both teams must be determined from previous rounds.");
        }
        
        this.state = MatchState.IN_PROGRESS;
    }

    /**
     * Records the final scores for both sides and determines the winner.
     * Transitions the match to COMPLETED state.
     * @param score1 Score for the left-side participant.
     * @param score2 Score for the right-side participant.
     * @throws IllegalStateException if participants are not yet determined, the match
     *                               is already completed, or the scores are tied.
     */
    public void finalizeScores(int score1, int score2) {
        if (!isReady()) throw new IllegalStateException("Match waiting on previous rounds.");
        if (state == MatchState.COMPLETED) throw new IllegalStateException("Match already completed.");
        
        Team t1 = leftChild.getWinner();
        Team t2 = rightChild.getWinner();
        
        if (score1 > score2) winner = t1;
        else if (score2 > score1) winner = t2;
        else throw new IllegalStateException("Ties not allowed.");
        
        this.state = MatchState.COMPLETED;
    }

    public String getMatchId() { 
        return matchId; 
    }
    
    public MatchState getState() { 
        return state; 
    }
    
    /**
     * Returns a single-line summary of this match for the results screen.
     * Format: {@code <matchId>  <[DONE|READY|WAITING]> : <left>  vs  <right>}
     */
    @Override
    public String toString() {
        String status = isReady() ? (state == MatchState.COMPLETED ? "[DONE]" : "[READY]") : "[WAITING]";
        return String.format("%-8s %-10s : %s  vs  %s", 
            matchId, status, leftChild.getDisplayName(), rightChild.getDisplayName());
    }
}