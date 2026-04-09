package com.esportstournamentscheduler.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esportstournamentscheduler.application.factory.IPlayerFactory;
import com.esportstournamentscheduler.application.factory.ITeamFactory;
import com.esportstournamentscheduler.domain.model.Match;
import com.esportstournamentscheduler.domain.model.Player;
import com.esportstournamentscheduler.domain.model.Team;
import com.esportstournamentscheduler.domain.model.Tournament;
import com.esportstournamentscheduler.domain.policy.ITeamValidationPolicy;

/**
 * Central orchestrator for the esports tournament system.
 * Manages the global pool of teams and tournaments, delegates team/player
 * creation to the injected factories, enforces team naming rules via the
 * active validation policy, and drives tournament lifecycle operations
 * (start, resolve matches, advance bracket).
 */
public class TournamentManager 
{
    /** The tournament currently in focus for bracket and match operations. */
    private Tournament currentlySelectedTournament;

    /** All tournaments created this session, keyed by tournament name. */
    private Map<String, Tournament> tournaments;

    /** Global pool of teams available for registration, keyed by team name. */
    private Map<String, Team> teams = new HashMap<>();

    /** Factory used to construct new Team instances. */
    private final ITeamFactory teamFactory;

    /** Factory used to construct new Player instances. */
    private final IPlayerFactory playerFactory;

    /** Strategy controlling team name uniqueness and size validation rules. */
    private ITeamValidationPolicy teamValidationPolicy;

    /**
     * Constructs a TournamentManager with the specified factories and validation policy.
     * @param teamFactory           Factory for creating Team instances.
     * @param playerFactory         Factory for creating Player instances.
     * @param teamValidationPolicy  Strategy for validating team names and sizes.
     */
    public TournamentManager(ITeamFactory teamFactory, IPlayerFactory playerFactory, ITeamValidationPolicy teamValidationPolicy) {
        this.teamFactory = teamFactory;
        this.playerFactory = playerFactory;
        this.teamValidationPolicy = teamValidationPolicy;
        tournaments = new HashMap<>();
    }

    /**
     * Creates a new team and adds it to the global team pool.
     * Validates name uniqueness via the active policy before creation.
     * @param teamName The desired name for the new team.
     * @throws IllegalStateException    if the name is already taken (policy violation).
     * @throws IllegalArgumentException if the name duplicates an existing team key.
     */
    public void CreateTeam(String teamName) 
    {
        // Check if the teams name already exists in the tournament
        teamValidationPolicy.validateUniqueTeamName(teamName, teams.keySet());
        
        Team newTeam = teamFactory.createTeam(teamName);
        
        if(teams.containsKey(newTeam.getName())) throw new IllegalArgumentException("Team name already exists.");
        
        teams.put(newTeam.getName(), newTeam);
    }
    
    /**
     * Creates a new Tournament and registers it in the tournament pool.
     * @param tournamentName    The unique display name for the tournament.
     * @param gameName          The game being played.
     * @param numOfTeams        The required number of participating teams (must be 4 or 8).
     * @param maxPlayersPerTeam The maximum number of players allowed per team.
     * @throws IllegalArgumentException if {@code numOfTeams} is not 4 or 8, or
     *                                  if {@code maxPlayersPerTeam} is less than 1.
     */
    public void CreateTournament(String tournamentName, String gameName, int numOfTeams, int maxPlayersPerTeam) 
    {
        Tournament newTournament = new Tournament(tournamentName, numOfTeams, maxPlayersPerTeam, gameName);
        tournaments.put(tournamentName, newTournament);
    }

    /**
     * Creates a player via the factory and appends them to the specified team's roster.
     * @param teamName   The name of the team to add the player to.
     * @param playerName The display name of the new player.
     * @throws IllegalArgumentException if no team with {@code teamName} exists.
     */
    public void addPlayerToTeam(String teamName, String playerName) {
        Team team = teams.get(teamName);
        
        if (team == null) {
            throw new IllegalArgumentException("Team '" + teamName + "' does not exist.");
        }

        // Use the factory to create the player, then add them to the team
        Player newPlayer = playerFactory.createPlayer(playerName);
        team.addPlayer(newPlayer);
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public Map<String, Tournament> getTournaments() {
        return tournaments;
    }

    /**
     * Prints all teams in the global pool to standard output, including their rosters.
     * Outputs a message if no teams have been created yet.
     */
    public void printAllTeams() {
        if (teams.isEmpty()) {
            System.out.println("No teams have been created yet.");
            return;
        }
        System.out.println("Current Teams:");
        for (Team team : teams.values()) {
            System.out.println(team);
        }
    }

    /**
     * Starts the currently selected tournament, generating the bracket and
     * auto-starting all first-round matches.
     * @throws IllegalStateException if no tournament has been selected, or if the
     *                               tournament cannot be started (wrong state or team count).
     */
    public void startActiveTournament() {
        if (this.currentlySelectedTournament == null) {
            throw new IllegalStateException("No tournament created yet.");
        }
        this.currentlySelectedTournament.startTournament();
    }

    /**
     * Records the result of a match by ID and advances the bracket.
     * Accepts either "Match N" or just "N" as the match ID.
     * After scores are finalized, {@link Tournament#advanceReadyMatches()} is called
     * to start any next-round matches whose participants are now determined.
     * @param matchId The match identifier (e.g., "Match 1" or "1").
     * @param score1  Score for the left (first) participant.
     * @param score2  Score for the right (second) participant.
     * @throws IllegalStateException    if no tournament is active, or the match state
     *                                  prevents recording (already completed, tied scores, etc.).
     * @throws IllegalArgumentException if no match with the given ID exists.
     */
    public void resolveMatch(String matchId, int score1, int score2) 
    {
        // Check if a tournament is active
        if (this.currentlySelectedTournament == null) throw new IllegalStateException("No active tournament.");

        // Normalize match ID input (allow "1" instead of "Match 1", etc.)
        String normalized = matchId.trim();

        // Check if the input is purely numeric, and if so, prepend "Match "
        if (normalized.matches("\\d+")) normalized = "Match " + normalized;

        Match targetMatch = null;

        // Search through all matches in the current tournament to find the one with the matching ID
        for (List<Match> round : currentlySelectedTournament.getBracketRounds()) 
        {
            // For each match in the round, check if its ID matches the normalized input
            for (Match m : round) 
            {
                // Check if the match ID matches the normalized input (case-insensitive)
                if (m.getMatchId().equalsIgnoreCase(normalized)) 
                {
                    // Target match found, break out of the loops
                    targetMatch = m;
                    break;
                }
            }
            
            // If we've found the target match, no need to continue searching through rounds
            if (targetMatch != null) break;
        }

        // If we finish searching all matches and haven't found a match with the given ID, throw an error
        if (targetMatch == null) 
        {
            throw new IllegalArgumentException("Match ID '" + matchId + "' not found. Try Match 1, Match 2, etc.");
        }

        targetMatch.finalizeScores(score1, score2);
        
        // Advance the bracket by starting any matches in the next round that are now ready
        currentlySelectedTournament.advanceReadyMatches();
    }

    /**
     * Changes the team validation policy for a specific tournament.
     * Only permitted while the tournament is still in the REGISTRATION phase.
     * @param tournamentName The name of the tournament to update.
     * @param policy         The new policy to apply.
     * @throws IllegalArgumentException if the tournament is not found.
     * @throws IllegalStateException    if the tournament has already started.
     */
    public void setTournamentTeamPolicy(String tournamentName, ITeamValidationPolicy policy) {
        if (!tournaments.containsKey(tournamentName)) {
            throw new IllegalArgumentException("Tournament '" + tournamentName + "' not found.");
        }
        tournaments.get(tournamentName).setTeamValidationPolicy(policy);
    }

    // --- Bracket operations ---

    /**
     * Sets the currently selected tournament by tournament name.
     * @param tournamentName The name of the tournament to select
     */
    public void setSelectedTournament(String tournamentName) 
    {
        if (tournaments.containsKey(tournamentName)) 
        {
            this.currentlySelectedTournament = tournaments.get(tournamentName);
        } 
        else 
        {
            throw new IllegalArgumentException("Tournament '" + tournamentName + "' not found.");
        }
    }

    /**
     * Registers a team to a tournament by their names.
     * @param tournamentName The name of the tournament to add the team to
     * @param teamName The name of the team to register
     * @throws IllegalArgumentException if tournament or team is not found
     */
    public void registerTeamToTournament(String tournamentName, String teamName) {
        if (!tournaments.containsKey(tournamentName)) {
            throw new IllegalArgumentException("Tournament '" + tournamentName + "' not found.");
        }
        
        if (!teams.containsKey(teamName)) {
            throw new IllegalArgumentException("Team '" + teamName + "' not found.");
        }
        
        Tournament tournament = tournaments.get(tournamentName);
        Team team = teams.get(teamName);
        
        tournament.registerTeam(team);
    }

    /**
     * Removes a team from a tournament by their names.
     * @param tournamentName The name of the tournament to remove the team from
     * @param teamName The name of the team to unregister
     * @throws IllegalArgumentException if tournament or team is not found
     * @throws IllegalStateException if tournament is not in registration phase
     */
    public void removeTeamFromTournament(String tournamentName, String teamName) {
        if (!tournaments.containsKey(tournamentName)) {
            throw new IllegalArgumentException("Tournament '" + tournamentName + "' not found.");
        }
        
        Tournament tournament = tournaments.get(tournamentName);
        tournament.removeTeam(teamName);
    }

    // --- ASCII visual bracket ---

    /**
     * Prints the ASCII bracket for the currently selected tournament to standard output,
     * including match states, current winners, and the overall champion once the
     * final match is resolved. Calls {@link Tournament#endTournament()} when a champion
     * is determined.
     */
   public void printVisualBracket() {
    if (currentlySelectedTournament == null || currentlySelectedTournament.getBracketRounds().isEmpty()) {
        System.out.println("Bracket not generated yet.");
        return;
    }

    List<String> lines = buildTreeBracketLines();
    System.out.println();
    System.out.println("==============================================================");
    System.out.println("BRACKET: " + currentlySelectedTournament.getName() + " | GAME: " + currentlySelectedTournament.getGame());
    System.out.println("==============================================================");
    for (String line : lines) {
        System.out.println(line);
    }

    Match finalMatch = currentlySelectedTournament.getBracketRounds()
        .get(currentlySelectedTournament.getBracketRounds().size() - 1)
        .get(0);
    String champion;
    if(finalMatch.getWinner() == null){
        champion = "TBD";
    }
    else {
        champion = finalMatch.getWinner().getName();
        currentlySelectedTournament.endTournament();
    }

    System.out.println("--------------------------------------------------------------");
    System.out.println("CHAMPION: " + champion);
    System.out.println("==============================================================");
    System.out.println();
}

    /**
     * Builds the bracket as a list of padded strings ready for line-by-line printing.
     * Uses a character canvas approach: each bracket position is computed from its
     * row/column index, connectors drawn with ASCII art (|, -, +), and text labels
     * placed at the midpoint row of each match.
     * @return An ordered list of trimmed strings representing the bracket display.
     */
    private List<String> buildTreeBracketLines() {
    List<List<Match>> rounds = currentlySelectedTournament.getBracketRounds();
    int teamCount = currentlySelectedTournament.getMaxTeams();

    int rows = teamCount * 2 - 1;
    int nameColWidth = 18;
    int roundColStep = 22;
    int cols = nameColWidth + (rounds.size() * roundColStep) + 30;

    char[][] canvas = new char[rows][cols];
    for (int r = 0; r < rows; r++) {
        java.util.Arrays.fill(canvas[r], ' ');
    }

    // Seed team names from round 1 match leaves.
    List<String> teamNames = new java.util.ArrayList<>();
    for (Match m : rounds.get(0)) {
        teamNames.add(m.getLeftNode().getDisplayName());
        teamNames.add(m.getRightNode().getDisplayName());
    }
    for (int i = 0; i < teamNames.size(); i++) {
        putText(canvas, i * 2, 0, fit(teamNames.get(i), nameColWidth - 1));
    }

    for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
        List<Match> roundMatches = rounds.get(roundIndex);
        int groupSize = 1 << (roundIndex + 1);
        int anchorCol = nameColWidth + (roundIndex * roundColStep);

        for (int matchIndex = 0; matchIndex < roundMatches.size(); matchIndex++) {
            Match m = roundMatches.get(matchIndex);

            int topTeamIndex = matchIndex * groupSize;
            int bottomTeamIndex = topTeamIndex + groupSize - 1;

            int topRow = topTeamIndex * 2;
            int bottomRow = bottomTeamIndex * 2;
            int midRow = (topRow + bottomRow) / 2;

            // Vertical trunk for this match connector
            for (int r = topRow + 1; r < bottomRow; r++) {
                canvas[r][anchorCol - 2] = '|';
            }

            // Horizontal arms into the trunk
            for (int c = anchorCol - 6; c < anchorCol - 2; c++) {
                canvas[topRow][c] = '-';
                canvas[bottomRow][c] = '-';
            }

            canvas[topRow][anchorCol - 2] = '+';
            canvas[bottomRow][anchorCol - 2] = '+';
            canvas[midRow][anchorCol - 2] = '+';

            for (int c = anchorCol - 1; c < anchorCol + 2; c++) {
                canvas[midRow][c] = '-';
            }

            String winner = m.getWinner() == null ? "TBD" : m.getWinner().getName();
            String label = String.format("%s [%s] %s",
                m.getMatchId(),
                m.getState().name(),
                fit(winner, 12)
            );
            putText(canvas, midRow, anchorCol + 3, label);
        }
    }

    List<String> out = new java.util.ArrayList<>();
    for (int r = 0; r < rows; r++) {
        out.add(rtrim(new String(canvas[r])));
    }
    return out;
}

    /**
     * Writes {@code text} onto the character canvas at the given row and column.
     * Silently skips characters that fall outside the canvas bounds.
     * @param canvas The 2D character grid being built.
     * @param row    Target row index.
     * @param col    Starting column index.
     * @param text   The text to place on the canvas.
     */
    private void putText(char[][] canvas, int row, int col, String text) {
    if (row < 0 || row >= canvas.length) return;
    for (int i = 0; i < text.length() && col + i < canvas[row].length; i++) {
        if (col + i >= 0) {
            canvas[row][col + i] = text.charAt(i);
        }
    }
}

    /**
     * Truncates or left-pads {@code value} to exactly {@code width} characters.
     * If truncated, the last character is replaced with '.' to indicate overflow.
     * @param value The string to fit.
     * @param width The exact desired character width.
     * @return A string of exactly {@code width} characters.
     */
    private String fit(String value, int width) {
    if (value == null) value = "";
    if (value.length() > width) return value.substring(0, width - 1) + ".";
    return String.format("%-" + width + "s", value);
}

    /**
     * Removes trailing space characters from a string.
     * Used to clean up canvas rows before printing.
     * @param s The string to trim.
     * @return The string with all trailing spaces removed.
     */
    private String rtrim(String s) {
    int end = s.length();
    while (end > 0 && s.charAt(end - 1) == ' ') end--;
    return s.substring(0, end);
}
}