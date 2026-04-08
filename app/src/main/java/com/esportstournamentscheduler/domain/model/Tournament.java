package com.esportstournamentscheduler.domain.model;
import com.esportstournamentscheduler.domain.policy.ITeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.StrictTeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.ITournamentValidationPolicy;
import com.esportstournamentscheduler.domain.policy.TournamentValidationPolicy;
import com.esportstournamentscheduler.domain.bracket.IBracketNode;
import com.esportstournamentscheduler.domain.bracket.TeamNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament 
{
    private final String name;                  // Tournament name
    private final String game;                  // Game being played in the tournament
    private final List<Team> registeredTeams;   // List of teams registered for the tournament
    private final List<Match> matches;          // List of matches in the tournament (can be used to track match results and scheduling)
    private Map<String, Team> teamMap;          // Holds the teams - Uses map for quick lookup by ID
    
    private final int REQUIRED_NUMBER_TEAMS;    // The required number of teams for the tournament
    private final int MAX_PLAYERS_PER_TEAM;     // The maximum number of players allowed per team

    // Validation policies for teams and the tournament
    private ITeamValidationPolicy teamValidationPolicy; 
    private ITournamentValidationPolicy tournamentValidationPolicy;

    // Enum to represent the state of the tournament
    private enum TournamentState {
        REGISTRATION,
        IN_PROGRESS,
        COMPLETED
    }

    // Current state of the tournament
    private TournamentState state;

    /**
     * Constructs a new Tournament with the specified parameters.
     * @param name The name of the tournament.
     * @param numTeams The number of teams allowed in the tournament.
     * @param maxPlayersPerTeam The maximum number of players allowed per team.
     * @param game The game being played in the tournament.
     */
    public Tournament(String name, int numTeams, int maxPlayersPerTeam, String game) 
    {
        if(numTeams != 4 && numTeams != 8) throw new IllegalArgumentException("Number of teams must be 4 or 8.");
        if(maxPlayersPerTeam <= 0) throw new IllegalArgumentException("Max players per team must be greater than 0.");
        
        this.REQUIRED_NUMBER_TEAMS = numTeams;
        this.MAX_PLAYERS_PER_TEAM = maxPlayersPerTeam;
        
        this.name = name;
        this.game = game;

        this.teamValidationPolicy = new StrictTeamValidationPolicy(); // Default to strict policy, can be changed later
        this.tournamentValidationPolicy = new TournamentValidationPolicy(); // Default tournament validation policy
        
        this.registeredTeams = new ArrayList<>();
        this.matches = new ArrayList<>();        
        this.teamMap = new HashMap<>();
        
        this.state = TournamentState.REGISTRATION; 
    }

    private final List<List<Match>> bracketRounds = new ArrayList<>();

    public void startTournament() {
         if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to start.");
         tournamentValidationPolicy.validateNumberOfTeams(registeredTeams.size(), REQUIRED_NUMBER_TEAMS);
       
        
        Collections.shuffle(registeredTeams);
        bracketRounds.clear();

        // 1. Create the bottom layer (Team Nodes)
        List<IBracketNode> currentNodes = new ArrayList<>();
        for(Team team : registeredTeams) {
            currentNodes.add(new TeamNode(team));
        }

        // 2. Build the tree upward, round by round
        int matchCounter = 1;

        while (currentNodes.size() > 1) {
            List<IBracketNode> nextNodes = new ArrayList<>();
            List<Match> thisRoundMatches = new ArrayList<>();
            
            for (int i = 0; i < currentNodes.size(); i += 2) {
                IBracketNode left = currentNodes.get(i);
                IBracketNode right = currentNodes.get(i + 1);
                
                String mId = "Match " + matchCounter++;
                Match newMatch = new Match(mId, left, right);
                
                nextNodes.add(newMatch);
                thisRoundMatches.add(newMatch);
            }
            
            bracketRounds.add(thisRoundMatches); // Save the round for printing
            currentNodes = nextNodes; // Move up the tree
        }

        this.state = TournamentState.IN_PROGRESS;

    }

    public void endTournament() {
        if(state != TournamentState.IN_PROGRESS) throw new IllegalStateException("Tournament must be in progress to end.");
        this.state = TournamentState.COMPLETED;
    }

    public List<List<Match>> getBracketRounds() {
        return bracketRounds;
    }

    /**
     * Registers a team for the tournament. Must be called during the registration phase and will validate the team based on the current team validation policy.
     * @param team - The team to register. Must not be null and must meet the criteria defined by the current ITeamValidationPolicy.
     */
    public void registerTeam(Team team) {
        
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to register teams.");
        if(registeredTeams.size() >= REQUIRED_NUMBER_TEAMS) throw new IllegalStateException("Tournament is full. Cannot register more than " + REQUIRED_NUMBER_TEAMS + " teams.");
        
        teamValidationPolicy.validateTeamSize(team, MAX_PLAYERS_PER_TEAM);
        teamValidationPolicy.validateUniqueTeamName(team.getName(), teamMap.keySet());

        registeredTeams.add(team);
        teamMap.put(team.getName(), team);
    }
    
    public List<Team> getRegisteredTeams() { 
        return registeredTeams; 
    }

    public List<Match> getMatches() { 
        return matches; 
    }
    
    public String getName() { 
        return name; 
    }

    public String getGame() {
        return game;
    }

    public int getMaxTeams() {
        return REQUIRED_NUMBER_TEAMS;
    }

    public int getMaxPlayersPerTeam() {
        return MAX_PLAYERS_PER_TEAM;
    }

    public boolean isRegistrationOpen() {
        return state == TournamentState.REGISTRATION;
    }

    public boolean isInProgress() {
        return state == TournamentState.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return state == TournamentState.COMPLETED;
    }

    public void removeTeam(String teamName) {
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to remove teams.");
        if(!teamMap.containsKey(teamName)) throw new IllegalArgumentException("Team name does not exist.");
        Team teamToRemove = teamMap.get(teamName);
        registeredTeams.remove(teamToRemove);
        teamMap.remove(teamName);
    }

     public void clearTeams() {
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to clear teams.");
        registeredTeams.clear();
        teamMap.clear();
    }

    
}