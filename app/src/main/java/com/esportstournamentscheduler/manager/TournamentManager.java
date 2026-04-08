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
    private Tournament tournament;
    private Map<String, Team> teams = new HashMap<>();

    private final ITeamFactory teamFactory;
    private final IPlayerFactory playerFactory;
    private ITeamValidationPolicy teamValidationPolicy;

    public TournamentManager(ITeamFactory teamFactory, IPlayerFactory playerFactory, ITeamValidationPolicy teamValidationPolicy) {
        this.teamFactory = teamFactory;
        this.playerFactory = playerFactory;
        this.teamValidationPolicy = teamValidationPolicy;
        tournament = new Tournament("Default Tournament", 8,5, "Default Game");
    }

    public void CreateTeam(String teamName) {

        teamValidationPolicy.validateUniqueTeamName(teamName, teams.keySet());

        Team newTeam = teamFactory.createTeam(teamName);
        if(teams.containsKey(newTeam.getName())) throw new IllegalArgumentException("Team name already exists.");
        teams.put(newTeam.getName(), newTeam);
    }
    
    public void addPlayerToTeam(String teamName, String playerName) {
        Team team = teams.get(teamName);
        
        if (team == null) {
            throw new IllegalArgumentException("Team '" + teamName + "' does not exist.");
        }
        
        if (team.getPlayers().size() >= tournament.getMaxPlayersPerTeam()) {
            throw new IllegalStateException("Team '" + teamName + "' is full. Maximum " + tournament.getMaxPlayersPerTeam() + " players allowed.");
        }

        // Use the factory to create the player, then add them to the team
        Player newPlayer = playerFactory.createPlayer(playerName);
        team.addPlayer(newPlayer);
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void clearTeams() {
        teams.clear();
    }

    public void removeTeam(String teamName) {
        if(!teams.containsKey(teamName)) throw new IllegalArgumentException("Team name does not exist.");
        teams.remove(teamName);
    }

    public void ChangePolicy(ITeamValidationPolicy newPolicy) 
    {
        if(newPolicy == null) throw new IllegalArgumentException("Validation policy cannot be null.");
        this.teamValidationPolicy = newPolicy;
    }

    // --- BRACKET ENGINE HOOKS ---
    public void createTournamentFromSavedTeams(String tournamentName, String gameName) {
        int teamCount = teams.size();
        
        // 1. Validate we have a legal number of teams for a bracket
        if (teamCount != 4 && teamCount != 8) {
            throw new IllegalStateException("You must have exactly 4 or 8 teams saved to create a tournament. You currently have " + teamCount + ".");
        }

        // 2. Create the tournament dynamically based on how many teams exist
        // (Assuming a default of 5 players max per team, but you can change this!)
        this.tournament = new Tournament(tournamentName, teamCount, 5, gameName);

        // 3. Auto-enroll every saved team into the new tournament
        for (Team team : teams.values()) {
            this.tournament.registerTeam(team);
        }
        
        System.out.println("Successfully created '" + tournamentName + "' and auto-enrolled all " + teamCount + " teams!");
    }
    public void startActiveTournament() {
        if (this.tournament == null) {
            throw new IllegalStateException("No tournament created yet.");
        }
        this.tournament.startTournament();
    }
        public void resolveMatch(String matchId, int score1, int score2) {
        if (this.tournament == null) {
        throw new IllegalStateException("No active tournament.");
        }

        String normalized = matchId.trim();
        if (normalized.matches("\\d+")) {
        normalized = "Match " + normalized;
    }

    Match targetMatch = null;
    for (List<Match> round : tournament.getBracketRounds()) {
    for (Match m : round) {
    if (m.getMatchId().equalsIgnoreCase(normalized)) {
    targetMatch = m;
    break;
    }
    }
    if (targetMatch != null) break;
    }

    if (targetMatch == null) {
    throw new IllegalArgumentException("Match ID '" + matchId + "' not found. Try Match 1, Match 2, etc.");
    }

    targetMatch.finalizeScores(score1, score2);
    }

    // --- NEW: AUTO-CREATE TOURNAMENT ---

    // --- UPDATED: ASCII VISUAL BRACKET ---
   public void printVisualBracket() {
    if (tournament == null || tournament.getBracketRounds().isEmpty()) {
        System.out.println("Bracket not generated yet.");
        return;
    }

    List<String> lines = buildTreeBracketLines();
    System.out.println();
    System.out.println("==============================================================");
    System.out.println("BRACKET: " + tournament.getName() + " | GAME: " + tournament.getGame());
    System.out.println("==============================================================");
    for (String line : lines) {
        System.out.println(line);
    }

    Match finalMatch = tournament.getBracketRounds()
        .get(tournament.getBracketRounds().size() - 1)
        .get(0);
    String champion = finalMatch.getWinner() == null ? "TBD" : finalMatch.getWinner().getName();

    System.out.println("--------------------------------------------------------------");
    System.out.println("CHAMPION: " + champion);
    System.out.println("==============================================================");
    System.out.println();
}

private List<String> buildTreeBracketLines() {
    List<List<Match>> rounds = tournament.getBracketRounds();
    int teamCount = tournament.getMaxTeams();

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