package com.esportstournamentscheduler.domain.model;

import com.esportstournamentscheduler.domain.bracket.IBracketNode; // Make sure this import matches your folder structure!

public class Match implements IBracketNode {
    private final String matchId;
    
    // The Composite Tree Nodes
    private final IBracketNode leftChild;
    private final IBracketNode rightChild;
    
    private Team winner;
    
    public enum MatchState { PENDING, COMPLETED }
    private MatchState state;

    // Constructor
    public Match(String matchId, IBracketNode leftChild, IBracketNode rightChild) {
        this.matchId = matchId;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.state = MatchState.PENDING;
    }

    // --- NEW GETTERS FOR THE BRACKET PRINTER ---
    public IBracketNode getLeftNode() { 
        return leftChild; 
    }
    
    public IBracketNode getRightNode() { 
        return rightChild; 
    }

    // --- IBracketNode Implementation ---
    @Override
    public Team getWinner() { 
        return winner; 
    }

    @Override
    public boolean isReady() {
        return leftChild.getWinner() != null && rightChild.getWinner() != null;
    }

    @Override
    public String getDisplayName() {
        if (state == MatchState.COMPLETED && winner != null) {
            return winner.getName();
        }
        return "Winner of " + matchId;
    }

    // --- Core Logic ---
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
    
    @Override
    public String toString() {
        String status = isReady() ? (state == MatchState.COMPLETED ? "[DONE]" : "[READY]") : "[WAITING]";
        return String.format("%-8s %-10s : %s  vs  %s", 
            matchId, status, leftChild.getDisplayName(), rightChild.getDisplayName());
    }
}