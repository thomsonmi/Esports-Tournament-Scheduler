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
 * Manages the operations related to a tournament, including team and player management.
 */
public class TournamentManager 
{
    private Tournament currentlySelectedTournament;
    private Map<String, Tournament> tournaments;
    private Map<String, Team> teams = new HashMap<>();

    private final ITeamFactory teamFactory;
    private final IPlayerFactory playerFactory;
    private ITeamValidationPolicy teamValidationPolicy;

    public TournamentManager(ITeamFactory teamFactory, IPlayerFactory playerFactory, ITeamValidationPolicy teamValidationPolicy) {
        this.teamFactory = teamFactory;
        this.playerFactory = playerFactory;
        this.teamValidationPolicy = teamValidationPolicy;
        tournaments = new HashMap<>();
    }

    public void CreateTeam(String teamName) 
    {
        // Check if the teams name already exists in the tournament
        teamValidationPolicy.validateUniqueTeamName(teamName, teams.keySet());
        
        Team newTeam = teamFactory.createTeam(teamName);
        
        if(teams.containsKey(newTeam.getName())) throw new IllegalArgumentException("Team name already exists.");
        
        teams.put(newTeam.getName(), newTeam);
    }
    
    public void CreateTournament(String tournamentName, String gameName, int numOfTeams) 
    {
        Tournament newTournament = new Tournament(tournamentName, numOfTeams, 1, gameName);
        tournaments.put(tournamentName, newTournament);
    }

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

    public void startActiveTournament() {
        if (this.currentlySelectedTournament == null) {
            throw new IllegalStateException("No tournament created yet.");
        }
        this.currentlySelectedTournament.startTournament();
    }

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

    // --- NEW: AUTO-CREATE TOURNAMENT ---

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

    // --- UPDATED: ASCII VISUAL BRACKET ---
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

private void putText(char[][] canvas, int row, int col, String text) {
    if (row < 0 || row >= canvas.length) return;
    for (int i = 0; i < text.length() && col + i < canvas[row].length; i++) {
        if (col + i >= 0) {
            canvas[row][col + i] = text.charAt(i);
        }
    }
}

private String fit(String value, int width) {
    if (value == null) value = "";
    if (value.length() > width) return value.substring(0, width - 1) + ".";
    return String.format("%-" + width + "s", value);
}

private String rtrim(String s) {
    int end = s.length();
    while (end > 0 && s.charAt(end - 1) == ' ') end--;
    return s.substring(0, end);
}
}