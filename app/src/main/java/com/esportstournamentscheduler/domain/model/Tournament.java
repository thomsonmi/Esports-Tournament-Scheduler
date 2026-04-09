package com.esportstournamentscheduler.domain.model;
import com.esportstournamentscheduler.domain.policy.FlexibleTeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.ITeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.ITournamentValidationPolicy;
import com.esportstournamentscheduler.domain.policy.TournamentValidationPolicy;
import com.esportstournamentscheduler.domain.bracket.IBracketNode;
import com.esportstournamentscheduler.domain.bracket.TeamNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core domain model representing a single esports tournament.
 * Manages team registration, bracket generation, match progression,
 * and lifecycle state transitions (REGISTRATION → IN_PROGRESS → COMPLETED).
 * Uses the Strategy pattern for team and tournament validation, and the
 * Composite pattern for the bracket tree structure.
 */
public class Tournament 
{
    private final String name;                  // Tournament name
    private final String game;                  // Game being played in the tournament
    private final List<Team> registeredTeams;   // List of teams registered for the tournament
    private Map<String, Team> teamMap;          // Holds the teams - Uses map for quick lookup by ID
    
    private final int REQUIRED_NUMBER_TEAMS;    // The required number of teams for the tournament
    private final int MAX_PLAYERS_PER_TEAM;     // The maximum number of players allowed per team

    // Validation policies for teams and the tournament
    private ITeamValidationPolicy teamValidationPolicy; 
    private ITournamentValidationPolicy tournamentValidationPolicy;

    // Enum to represent the current lifecycle phase of the tournament.
    private enum TournamentState {
        REGISTRATION,   // Teams can be added or removed; bracket not yet generated.
        IN_PROGRESS,    // Bracket generated; matches are being played.
        COMPLETED       // All matches resolved; champion determined.
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

        this.teamValidationPolicy = new FlexibleTeamValidationPolicy(); // Default policy: 1 to max players allowed
        this.tournamentValidationPolicy = new TournamentValidationPolicy(); // Default tournament validation policy
        
        this.registeredTeams = new ArrayList<>();
        this.teamMap = new HashMap<>();

        this.state = TournamentState.REGISTRATION; 
    }

    private final List<List<Match>> bracketRounds = new ArrayList<>();

    /**
     * Builds the elimination bracket and transitions the tournament to IN_PROGRESS.
     * Registered teams are shuffled for random seeding, then paired into Match nodes
     * bottom-up until a single root match (the final) remains. All first-round
     * matches are auto-started once the bracket is built.
     * @throws IllegalStateException if the tournament is not in REGISTRATION state,
     *                               or if the registered team count does not match
     *                               the required count.
     */
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
        
        // Auto-start all first-round matches
        if (!bracketRounds.isEmpty()) {
            List<Match> firstRound = bracketRounds.get(0);
            for (Match match : firstRound) {
                try {
                    match.startMatch();
                } catch (IllegalStateException e) {
                    // If a match can't start, log it but continue with others
                    System.out.println("Warning: Could not start " + match.getMatchId() + " - " + e.getMessage());
                }
            }
        }

    }

    /**
     * Advances the bracket by starting all matches in the next round that are ready.
     * A match is ready when both its child matches have completed and have winners.
     * This should be called after recording match results to cascade the bracket progression.
     */
    public void advanceReadyMatches() {
        if (bracketRounds.isEmpty()) {
            return;
        }
        
        // Iterate through each round except the last (finals)
        for (int roundIndex = 0; roundIndex < bracketRounds.size() - 1; roundIndex++) {
            List<Match> currentRound = bracketRounds.get(roundIndex);
            List<Match> nextRound = bracketRounds.get(roundIndex + 1);
            
            // Check if all matches in current round are completed
            boolean allCurrentRoundComplete = currentRound.stream()
                .allMatch(m -> m.getState() == Match.MatchState.COMPLETED);
            
            // If current round is complete, start ready matches in next round
            if (allCurrentRoundComplete) {
                for (Match match : nextRound) {
                    if (match.getState() == Match.MatchState.PENDING && match.isReady()) {
                        try {
                            match.startMatch();
                        } catch (IllegalStateException e) {
                            // Match not ready yet, skip it
                        }
                    }
                }
            }
        }
    }

    /**
     * Transitions the tournament from IN_PROGRESS to COMPLETED.
     * Called automatically by the bracket printer once a champion is determined.
     * @throws IllegalStateException if the tournament is not currently IN_PROGRESS.
     */
    public void endTournament() {
        if(state != TournamentState.IN_PROGRESS) throw new IllegalStateException("Tournament must be in progress to end.");
        this.state = TournamentState.COMPLETED;
    }

    /**
     * Returns the bracket rounds in order from the first round to the final.
     * Each inner list contains all Matches played in that round.
     * The list is empty until {@link #startTournament()} has been called.
     * @return An ordered list of rounds, each containing that round's matches.
     */
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

    /**
     * Swaps the team validation policy used during team registration.
     * Can only be changed while the tournament is in the REGISTRATION phase.
     * @param policy The new policy to apply. Must not be null.
     * @throws IllegalStateException    if the tournament has already started.
     * @throws IllegalArgumentException if {@code policy} is null.
     */
    public void setTeamValidationPolicy(ITeamValidationPolicy policy) {
        if (state != TournamentState.REGISTRATION) throw new IllegalStateException("Policy can only be changed during the registration phase.");
        if (policy == null) throw new IllegalArgumentException("Policy cannot be null.");
        this.teamValidationPolicy = policy;
    }

    /**
     * Removes a registered team from the tournament by name.
     * @param teamName The name of the team to remove.
     * @throws IllegalStateException    if the tournament is not in REGISTRATION phase.
     * @throws IllegalArgumentException if no team with the given name is registered.
     */
    public void removeTeam(String teamName) {
        if(state != TournamentState.REGISTRATION) throw new IllegalStateException("Tournament must be in registration phase to remove teams.");
        if(!teamMap.containsKey(teamName)) throw new IllegalArgumentException("Team name does not exist.");
        Team teamToRemove = teamMap.get(teamName);
        registeredTeams.remove(teamToRemove);
        teamMap.remove(teamName);
    }

    
}