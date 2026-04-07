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

public class TournamentManager 
{
    private Tournament tournament;
    private Map<String, Team> teams = new HashMap<>();

    private final ITeamFactory teamFactory;
    private final IPlayerFactory playerFactory;

    public TournamentManager(ITeamFactory teamFactory, IPlayerFactory playerFactory) {
        this.teamFactory = teamFactory;
        this.playerFactory = playerFactory;
        tournament = new Tournament("Default Tournament", 8,5, "Default Game");
    }

    public void CreateTeam(String teamName) {
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

        System.out.println("\n==================================================================");
        System.out.println("                        OFFICIAL BRACKET");
        System.out.println("==================================================================\n");

        if (tournament.getMaxTeams() == 4) {
            print4TeamBracket();
        } else if (tournament.getMaxTeams() == 8) {
            print8TeamBracket();
        }
        
        System.out.println("\n==================================================================\n");
    }

    public void enrollTeamInTournament(String teamName) {
        if (this.tournament == null) throw new IllegalStateException("No active tournament.");
        Team team = teams.get(teamName);
        if (team == null) throw new IllegalArgumentException("Team does not exist.");
        this.tournament.registerTeam(team);
    }



 private String formatName(String name) {
        if (name.length() > 10) name = name.substring(0, 10); // Truncate long names
        return String.format("%-12s", name); // Pad with spaces to exactly 12 characters
    }

    private void print4TeamBracket() {
        List<Match> r1 = tournament.getBracketRounds().get(0);
        List<Match> r2 = tournament.getBracketRounds().get(1);

        Match m1 = r1.get(0); Match m2 = r1.get(1); 
        Match m3 = r2.get(0); // Finals

        String champ = m3.getWinner() != null ? m3.getWinner().getName() : "TBD";

        System.out.println(formatName(m1.getLeftNode().getDisplayName()) + " ──┐");
        System.out.println("                 ├── " + formatName(m3.getLeftNode().getDisplayName()) + " ──┐");
        System.out.println(formatName(m1.getRightNode().getDisplayName()) + " ──┘                 │");
        System.out.println("                                     ├── CHAMPION: " + champ);
        System.out.println(formatName(m2.getLeftNode().getDisplayName()) + " ──┐                 │");
        System.out.println("                 ├── " + formatName(m3.getRightNode().getDisplayName()) + " ──┘");
        System.out.println(formatName(m2.getRightNode().getDisplayName()) + " ──┘");
    }

    private void print8TeamBracket() {
        List<Match> r1 = tournament.getBracketRounds().get(0);
        List<Match> r2 = tournament.getBracketRounds().get(1);
        List<Match> r3 = tournament.getBracketRounds().get(2);

        Match m1 = r1.get(0); Match m2 = r1.get(1); Match m3 = r1.get(2); Match m4 = r1.get(3);
        Match m5 = r2.get(0); Match m6 = r2.get(1);
        Match m7 = r3.get(0); // Finals

        String champ = m7.getWinner() != null ? m7.getWinner().getName() : "TBD";

        System.out.println(formatName(m1.getLeftNode().getDisplayName()) + " ──┐");
        System.out.println("                 ├── " + formatName(m5.getLeftNode().getDisplayName()) + " ──┐");
        System.out.println(formatName(m1.getRightNode().getDisplayName()) + " ──┘                  │");
        System.out.println("                                      ├── " + formatName(m7.getLeftNode().getDisplayName()) + " ──┐");
        System.out.println(formatName(m2.getLeftNode().getDisplayName()) + " ──┐                  │                  │");
        System.out.println("                 ├── " + formatName(m5.getRightNode().getDisplayName()) + " ──┘                  │");
        System.out.println(formatName(m2.getRightNode().getDisplayName()) + " ──┘                                     │");
        System.out.println("                                                         ├── CHAMPION: " + champ);
        System.out.println(formatName(m3.getLeftNode().getDisplayName()) + " ──┐                                     │");
        System.out.println("                 ├── " + formatName(m6.getLeftNode().getDisplayName()) + " ──┐                  │");
        System.out.println(formatName(m3.getRightNode().getDisplayName()) + " ──┘                  │                  │");
        System.out.println("                                      ├── " + formatName(m7.getRightNode().getDisplayName()) + " ──┘");
        System.out.println(formatName(m4.getLeftNode().getDisplayName()) + " ──┐                  │");
        System.out.println("                 ├── " + formatName(m6.getRightNode().getDisplayName()) + " ──┘");
        System.out.println(formatName(m4.getRightNode().getDisplayName()) + " ──┘");
    }
}