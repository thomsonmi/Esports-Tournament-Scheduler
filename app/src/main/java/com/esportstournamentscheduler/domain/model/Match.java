package com.esportstournamentscheduler.domain.model;

public class Match {
    private final String matchId;
    private final Team team1;
    private final Team team2;
    private int team1Score;
    private int team2Score;
    private Team winner;
    private MatchState state;

    private enum MatchState {
        PENDING,
        ONGOING,
        COMPLETED
    }
    
    public Match(String matchId, Team team1, Team team2) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Score = 0;
        this.team2Score = 0;
        this.state = MatchState.PENDING;
    }
     // i think states will need to be implemented in this I have not done that 
    public void start() {
        this.state = MatchState.ONGOING;
       
    }

    public void complete() {
         this.state = MatchState.COMPLETED;
    }

    public void finalizeScores(int team1Score, int team2Score) 
    {
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        if (team1Score > team2Score) setWinner(team1);
        else if (team2Score > team1Score) setWinner(team2);
        else throw new IllegalStateException("Error! Tie detected. Please resolve and re-enter match score");
        complete();
    }
    
    public void setWinner(Team winner) { 
        this.winner = winner; 
    }
    public Team getWinner() { 
        return winner; 
    }
    
    public Team getTeam1() { 
        return team1; 
    }
    public Team getTeam2() { 
        return team2; 
    }
    public String getMatchId() { 
        return matchId; 
    }

    @Override
    // will change later to be more visually appealing 
    public String toString() {
        return 
        matchId + ": " + team1.getName() +
        " vs " + team2.getName() +
        " | Scores: " + team1Score + "-" + team2Score + 
               (winner != null ? " | Winner: " + winner.getName() : "");
    }
}