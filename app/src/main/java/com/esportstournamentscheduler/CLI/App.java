package com.esportstournamentscheduler.CLI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.esportstournamentscheduler.application.factory.IPlayerFactory;
import com.esportstournamentscheduler.application.factory.ITeamFactory;
import com.esportstournamentscheduler.application.factory.StandardPlayerFactory;
import com.esportstournamentscheduler.application.factory.StandardTeamFactory;
import com.esportstournamentscheduler.domain.model.Tournament;
import com.esportstournamentscheduler.domain.policy.FlexibleTeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.ITeamValidationPolicy;
import com.esportstournamentscheduler.domain.policy.StrictTeamValidationPolicy;
import com.esportstournamentscheduler.manager.TournamentManager;

/**
 * Main entry point and CLI controller for the Esports Tournament Scheduler.
 * Renders all user-facing menus via {@link SimpleMenuSelector}, reads input
 * from stdin, and delegates all business logic to {@link TournamentManager}.
 */
public class App {
    /** Shared scanner reading from stdin for the full application session. */
    private Scanner scanner = new Scanner(System.in);

    /** Central manager handling all tournament, team, and player operations. */
    private TournamentManager manager;


    /**
     * Constructs the application, wiring together the standard factories and
     * the flexible team validation policy into a new {@link TournamentManager}.
     */
    public App() {
       ITeamFactory teamFactory = new StandardTeamFactory();
       IPlayerFactory playerFactory = new StandardPlayerFactory();
         ITeamValidationPolicy teamValidationPolicy = new FlexibleTeamValidationPolicy();
         manager = new TournamentManager(teamFactory, playerFactory, teamValidationPolicy);
    }
    

    /**
     * Launches the main application loop, displaying the top-level menu and
     * routing user selections to the appropriate screen method until the user quits.
     */
public void start() {
        boolean running = true;

        // Use new simple menu selector
        List<String> menuOptions = Arrays.asList(
            "Create Tournament",
            "Select Tournament",
            "Create Team",
            "Bulk Create Teams",
            "View All Teams",
            "Quit"
        );
        while (running) 
        {     
            SimpleMenuSelector menu = new SimpleMenuSelector(menuOptions, scanner, null);
            int choice = menu.selectOption();

            switch (choice) {
                case 0: // Create Tournament
                    String[] tournamentData = promptForTournamentCreation();
                    try 
                    {
                        manager.CreateTournament(
                            tournamentData[0],
                            tournamentData[1],
                            Integer.parseInt(tournamentData[2]),
                            Integer.parseInt(tournamentData[3])
                        );
                        System.out.println("Tournament '" + tournamentData[0] + "' created successfully.");
                    } 
                    catch (IllegalArgumentException e) 
                    {
                        System.out.println("Error: " + e.getMessage());
                    }

                    break;                   
                    
                case 1: // Select Tournament
                    String selectedTournament = selectTournamentMenu();
                    if (selectedTournament != null) 
                    {
                        int tournamentChoice = 0;
                        boolean exitFlag = false;
                        while(!exitFlag) 
                        {
                            tournamentChoice = displayTournamentMenu(selectedTournament);
            
                            switch(tournamentChoice) 
                            {
                                case 0: // View Tournament Details
                                    displayTournamentDetails(selectedTournament);
                                    break;
                                case 1: // View Registered Teams
                                    displayRegisteredTeams(selectedTournament);
                                    break;
                                case 2: // Add Team to Tournament
                                    displayAddTeamToTournament(selectedTournament);
                                    break;
                                case 3: // Remove team from tournament
                                    displayRemoveTeamFromTournament(selectedTournament);
                                    break;
                                case 4: // Change Validation Policy
                                    displayChangePolicy(selectedTournament);
                                    break;

                                case 5: // Start Tournament
                                    try 
                                    {
                                        manager.setSelectedTournament(selectedTournament);
                                        manager.startActiveTournament();
                                        System.out.println("Tournament '" + selectedTournament + "' has been started.");
                                    } 
                                    catch (IllegalStateException e) 
                                    {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    catch (Exception e) 
                                    {
                                        System.out.println("An unexpected error occurred: " + e.getMessage());
                                    }
                                    break;
                                
                                case 6: // Record Match Results
                                    displayRecordMatchResults(selectedTournament);
                                    break;
                                
                                case 7: // Display Bracket
                                    displayTournamentBracket(selectedTournament);
                                    break;
                                case 8: // Back to Main Menu
                                    exitFlag = true;
                                    break;
                                default:
                                    System.out.println("Invalid option. Please select a valid menu item.");
                            }
                            
                            if (!exitFlag) 
                            {
                                System.out.print("Press ENTER to continue...");
                                scanner.nextLine();
                            }
                        }
                    }

                    break;
                    
                case 2: // Create Team
                    System.out.print("Enter the name of the new team: ");
                    String teamName = scanner.nextLine();
                    try {
                        manager.CreateTeam(teamName);
                        System.out.println("Success! Team '" + teamName + "' has been created.");
                        
                        System.out.print("How many players are on your team? ");
                        if (!scanner.hasNextInt()) 
                        {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.next(); 
                            continue; 
                        }
                        
                        int numPlayers = scanner.nextInt();
                        
                        if(numPlayers <= 1) {
                            System.out.println("A team must have at least 1 player. Setting number of players to 1.");
                            numPlayers = 1;
                        }

                        scanner.nextLine();
                        
                        // for demo purposes, we will auto-generate player names instead of asking the user to input each one
                        System.out.println("***Auto Adding***" + numPlayers + " Players.");
                        for (int i = 0; i < numPlayers; i++) {
                            String playerName = "Player " + (i + 1); // Auto-generate player names;
                            
                            try {
                                manager.addPlayerToTeam(teamName, playerName);
                                System.out.println("  -> Added '" + playerName + "' to '" + teamName + "'.");
                            } catch (IllegalStateException e) {
                                System.out.println("  -> Error: " + e.getMessage());
                                System.out.println("  -> Stopping player additions for this team.");
                                break; 
                            }
                        }
                        System.out.println("Team setup complete!");
                        
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3: // Bulk Create Teams
                    displayBulkCreateTeams();
                    break;
                case 4: // View All Teams
                    manager.printAllTeams();
                    break;                    
                
                case 5: // Quit
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;                    
            }
            
            if (running) 
            {
                System.out.print("Press ENTER to continue...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Prompts the user for tournament creation details.
     * @return A String array containing [tournamentName, gameName, numberOfTeams, maxPlayersPerTeam]
     */
    private String[] promptForTournamentCreation() {
        System.out.println("\n=== Create New Tournament ===");
        
        System.out.print("Enter a name for the Tournament: ");
        String tournamentName = scanner.nextLine();
        
        System.out.print("Which game is being played? ");
        String gameName = scanner.nextLine();
        
        System.out.print("How many teams will be participating? (4 or 8): ");
        String numberOfTeams = scanner.nextLine();

        System.out.print("Maximum players per team: ");
        String maxPlayersPerTeam = scanner.nextLine();
        
        System.out.println("=============================\n");
        
        return new String[]{tournamentName, gameName, numberOfTeams, maxPlayersPerTeam};
    }   
    
    /**
     * Displays a numbered list of tournaments and allows the user to select one.
     * @return The name of the selected tournament, or null if no tournaments exist
     */
    private String selectTournamentMenu() {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        
        if (tournaments.isEmpty()) {
            System.out.println("\n=== Select Tournament ===");
            System.out.println("No tournaments have been created yet.");
            System.out.println("===========================\n");
            return null;
        }
        
        List<String> tournamentNames = new ArrayList<>(tournaments.keySet());
        List<String> displayList = new ArrayList<>();
        
        // Build display list with tournament name and state
        for (String name : tournamentNames) {
            Tournament tournament = tournaments.get(name);
            String state = "";
            
            if (tournament.isRegistrationOpen()) {
                state = "Registration Open";
            } else if (tournament.isInProgress()) {
                state = "In Progress";
            } else if (tournament.isCompleted()) {
                state = "Completed";
            } else {
                state = "Unknown State";
            }
            
            displayList.add(name + " [" + state + "]");
        }
        
        SimpleMenuSelector tournamentMenu = new SimpleMenuSelector(displayList, scanner, "SELECT TOURNAMENT");
        int selectedIndex = tournamentMenu.selectOption();
        
        return tournamentNames.get(selectedIndex);
    }
    
    /**
     * Displays the tournament submenu with options for managing a specific tournament.
     * @param tournamentName The name of the selected tournament
     * @return The user's menu selection (0-6), or -1 if returning to main menu
     */
    private int displayTournamentMenu(String tournamentName) {
        List<String> tournamentOptions = Arrays.asList(
            "View Tournament Details",
            "View Registered Teams",
            "Add Team to Tournament",
            "Remove Team from Tournament",
            "Change Validation Policy",
            "Start Tournament",
            "Record Match Results",
            "View Tournament Bracket",
            "Back to Main Menu"
        );
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Tournament: " + tournamentName);
        System.out.println("=".repeat(40));
        
        SimpleMenuSelector menu = new SimpleMenuSelector(tournamentOptions, scanner, null);
        int choice = menu.selectOption();
        
        return choice;
    }
    
    /**
     * Displays detailed information about a specific tournament.
     * @param tournamentName The name of the tournament to display details for
     */
    private void displayTournamentDetails(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);
        
        if (tournament == null) {
            System.out.println("\nTournament '" + tournamentName + "' not found.");
            return;
        }

        // We need to determine the tournament state (active/inactive) based on its current phase        
         
        // Display tournament details in a formatted manner
        String tourneyState = "";

        if(tournament.isRegistrationOpen()) {
            tourneyState = "Registration Open";
        } else if (tournament.isInProgress()) {
            tourneyState = "In Progress";
        } else if (tournament.isCompleted()) {
            tourneyState = "Completed";
        } else {
            tourneyState = "Unknown State";
        }
            

        System.out.println("\n" + "=".repeat(50));
        System.out.println("TOURNAMENT DETAILS");
        System.out.println("=".repeat(50));
        System.out.println("Name:                    " + tournament.getName());
        System.out.println("Game:                    " + tournament.getGame());
        System.out.println("Required Teams:          " + tournament.getMaxTeams());
        System.out.println("Max Players Per Team:    " + tournament.getMaxPlayersPerTeam());
        System.out.println("Teams Registered:        " + tournament.getRegisteredTeams().size() + " / " + tournament.getMaxTeams());
        System.out.println("Tournament State:        " + tourneyState);
        System.out.println("=".repeat(50) + "\n");
    }
    
    /**
     * Displays all teams registered in a specific tournament.
     * @param tournamentName The name of the tournament to display teams for
     */
    private void displayRegisteredTeams(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);
        
        if (tournament == null) {
            System.out.println("\nTournament '" + tournamentName + "' not found.");
            return;
        }
        
        List<com.esportstournamentscheduler.domain.model.Team> registeredTeams = tournament.getRegisteredTeams();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("REGISTERED TEAMS - " + tournamentName);
        System.out.println("=".repeat(50));
        
        if (registeredTeams.isEmpty()) {
            System.out.println("No teams registered yet.");
        } else {
            int index = 1;
            for (com.esportstournamentscheduler.domain.model.Team team : registeredTeams) {
                System.out.println(index + ". " + team.getName() + " (" + team.getPlayers().size() + " players)");
                index++;
            }
        }
        
        System.out.println("=".repeat(50) + "\n");
    }
    
    /**
     * Displays the tournament bracket along with tournament state information.
     * @param tournamentName The name of the tournament to display the bracket for
     */
    private void displayTournamentBracket(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);
        
        if (tournament == null) {
            System.out.println("\nTournament '" + tournamentName + "' not found.");
            return;
        }
        
        if (tournament.getBracketRounds().isEmpty()) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("TOURNAMENT BRACKET - " + tournamentName);
            System.out.println("=".repeat(50));
            System.out.println("Bracket has not been generated yet.");
            System.out.println("Start the tournament to generate the bracket.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        // Display the visual bracket
        manager.setSelectedTournament(tournamentName);
        manager.printVisualBracket();
        
        // Display tournament state below the bracket
        String tourneyState = "";
        if (tournament.isRegistrationOpen()) {
            tourneyState = "Registration Open";
        } else if (tournament.isInProgress()) {
            tourneyState = "In Progress";
        } else if (tournament.isCompleted()) {
            tourneyState = "Completed";
        } else {
            tourneyState = "Unknown State";
        }
        
        System.out.println("Tournament State: " + tourneyState);
        System.out.println();
    }
    
    /**
     * Displays a screen for adding teams to a tournament.
     * Shows available teams not yet registered and allows user to select one to add.
     * @param tournamentName The name of the tournament to add teams to
     */
    private void displayAddTeamToTournament(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);
        
        if (tournament == null) {
            System.out.println("\nTournament '" + tournamentName + "' not found.");
            return;
        }
        
        java.util.Map<String, com.esportstournamentscheduler.domain.model.Team> availableTeams = manager.getTeams();
        List<com.esportstournamentscheduler.domain.model.Team> registeredTeams = tournament.getRegisteredTeams();
        
        // Filter out teams already registered
        List<com.esportstournamentscheduler.domain.model.Team> unregisteredTeams = new ArrayList<>();
        for (com.esportstournamentscheduler.domain.model.Team team : availableTeams.values()) {
            if (!registeredTeams.contains(team)) {
                unregisteredTeams.add(team);
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ADD TEAM TO TOURNAMENT - " + tournamentName);
        System.out.println("=".repeat(50));
        
        if (unregisteredTeams.isEmpty()) {
            System.out.println("No unregistered teams available.");
            System.out.println("All created teams are already registered or no teams exist yet.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        // Check if tournament is full
        if (registeredTeams.size() >= tournament.getMaxTeams()) {
            System.out.println("Tournament is full! (" + registeredTeams.size() + " / " + tournament.getMaxTeams() + " teams)");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        // Display available teams
        List<String> teamNames = new ArrayList<>();
        for (com.esportstournamentscheduler.domain.model.Team team : unregisteredTeams) {
            teamNames.add(team.getName() + " (" + team.getPlayers().size() + " players)");
        }
        teamNames.add("Cancel");
        
        SimpleMenuSelector menu = new SimpleMenuSelector(teamNames, scanner, null);
        int selectedIndex = menu.selectOption();
        
        // Handle cancel option
        if (selectedIndex == unregisteredTeams.size()) {
            System.out.println("Cancelled adding team.");
            return;
        }
        
        // Add the selected team
        com.esportstournamentscheduler.domain.model.Team selectedTeam = unregisteredTeams.get(selectedIndex);
        try {
            manager.registerTeamToTournament(tournamentName, selectedTeam.getName());
            System.out.println("\nSuccess! '" + selectedTeam.getName() + "' has been added to the tournament.");
            System.out.println("Teams registered: " + tournament.getRegisteredTeams().size() + " / " + tournament.getMaxTeams());
            System.out.println("=".repeat(50) + "\n");
        } catch (Exception e) {
            System.out.println("\nError adding team: " + e.getMessage());
            System.out.println("=".repeat(50) + "\n");
        }
    }
    
    /**
     * Displays a screen for removing a team from a tournament.
     * Shows registered teams and allows user to select one to remove.
     * @param tournamentName The name of the tournament
     */
    private void displayRemoveTeamFromTournament(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);
        
        if (tournament == null) {
            System.out.println("\nTournament '" + tournamentName + "' not found.");
            return;
        }
        
        List<com.esportstournamentscheduler.domain.model.Team> registeredTeams = tournament.getRegisteredTeams();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("REMOVE TEAM FROM TOURNAMENT - " + tournamentName);
        System.out.println("=".repeat(50));
        
        if (registeredTeams.isEmpty()) {
            System.out.println("No teams registered in this tournament.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        // Display registered teams
        List<String> teamNames = new ArrayList<>();
        for (com.esportstournamentscheduler.domain.model.Team team : registeredTeams) {
            teamNames.add(team.getName() + " (" + team.getPlayers().size() + " players)");
        }
        teamNames.add("Cancel");
        
        SimpleMenuSelector menu = new SimpleMenuSelector(teamNames, scanner, null);
        int selectedIndex = menu.selectOption();
        
        // Handle cancel option
        if (selectedIndex == registeredTeams.size()) {
            System.out.println("Cancelled removing team.");
            return;
        }
        
        // Remove the selected team
        com.esportstournamentscheduler.domain.model.Team selectedTeam = registeredTeams.get(selectedIndex);
        try {
            manager.removeTeamFromTournament(tournamentName, selectedTeam.getName());
            System.out.println("\nSuccess! '" + selectedTeam.getName() + "' has been removed from the tournament.");
            System.out.println("Teams registered: " + tournament.getRegisteredTeams().size() + " / " + tournament.getMaxTeams());
            System.out.println("=".repeat(50) + "\n");
        } catch (IllegalStateException e) {
            System.out.println("\nError removing team: " + e.getMessage());
            System.out.println("(Tournament must be in registration phase to remove teams)");
            System.out.println("=".repeat(50) + "\n");
        } catch (Exception e) {
            System.out.println("\nError removing team: " + e.getMessage());
            System.out.println("=".repeat(50) + "\n");
        }
    }
    
    /**
     * Displays a screen for recording match results.
     * Allows user to select a match and enter scores for both teams.
     * @param tournamentName The name of the tournament
     */
    private void displayRecordMatchResults(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);
        
        if (tournament == null) {
            System.out.println("\nTournament '" + tournamentName + "' not found.");
            return;
        }
        
        if (tournament.getBracketRounds().isEmpty()) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RECORD MATCH RESULTS");
            System.out.println("=".repeat(50));
            System.out.println("No matches available. Start the tournament first.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RECORD MATCH RESULTS - " + tournamentName);
        System.out.println("=".repeat(50));
        
        // Display all available matches
        System.out.println("\nAvailable Matches:\n");
        int matchCount = 0;
        for (java.util.List<com.esportstournamentscheduler.domain.model.Match> round : tournament.getBracketRounds()) {
            for (com.esportstournamentscheduler.domain.model.Match match : round) {
                if (match.getState() != com.esportstournamentscheduler.domain.model.Match.MatchState.COMPLETED) {
                    System.out.println(match.toString());
                    matchCount++;
                }
            }
        }
        
        if (matchCount == 0) {
            System.out.println("All matches are completed!");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        System.out.println("\nEnter match details:");
        
        // Get match ID
        System.out.print("Enter Match ID (e.g., 'Match 1' or just '1'): ");
        String matchId = scanner.nextLine();
        
        // Get team 1 score
        System.out.print("Enter Team 1 score: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        int score1 = scanner.nextInt();
        
        // Get team 2 score
        System.out.print("Enter Team 2 score: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        int score2 = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        // Record the match result
        try {
            manager.setSelectedTournament(tournamentName);
            manager.resolveMatch(matchId, score1, score2);
            System.out.println("\nSuccess! Match result recorded.");
            System.out.println("Bracket has been advanced.");
            System.out.println("=".repeat(50) + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
            System.out.println("=".repeat(50) + "\n");
        } catch (IllegalStateException e) {
            System.out.println("\nError: " + e.getMessage());
            System.out.println("=".repeat(50) + "\n");
        }
    }
    
    /**
     * Displays a screen for changing the team validation policy on a tournament.
     * Only available while the tournament is in the REGISTRATION phase.
     * Lets the user choose between Flexible (1 to max players) and Strict (exact player count).
     * @param tournamentName The name of the tournament to update.
     */
    private void displayChangePolicy(String tournamentName) {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        Tournament tournament = tournaments.get(tournamentName);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("CHANGE VALIDATION POLICY - " + tournamentName);
        System.out.println("=".repeat(50));

        if (tournament == null) {
            System.out.println("Tournament not found.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }

        if (!tournament.isRegistrationOpen()) {
            System.out.println("Policy can only be changed during the Registration phase.");
            System.out.println("Current state: " + (tournament.isInProgress() ? "In Progress" : "Completed"));
            System.out.println("=".repeat(50) + "\n");
            return;
        }

        System.out.println("Select a validation policy for team registration:\n");
        System.out.println("  Flexible  - Accepts teams with 1 up to the max number of players.");
        System.out.println("  Strict    - Requires every team to have exactly the max number of players.");
        System.out.println("  Max players per team for this tournament: " + tournament.getMaxPlayersPerTeam());
        System.out.println();

        List<String> policyOptions = Arrays.asList(
            "Flexible (1 to max players)",
            "Strict (exact player count required)",
            "Cancel"
        );

        SimpleMenuSelector menu = new SimpleMenuSelector(policyOptions, scanner, "CHOOSE POLICY");
        int choice = menu.selectOption();

        if (choice == 2) {
            System.out.println("Policy unchanged.");
            return;
        }

        ITeamValidationPolicy newPolicy = (choice == 0)
            ? new FlexibleTeamValidationPolicy()
            : new StrictTeamValidationPolicy();

        try {
            manager.setTournamentTeamPolicy(tournamentName, newPolicy);
            String policyName = (choice == 0) ? "Flexible" : "Strict";
            System.out.println("\nPolicy updated to: " + policyName);
            System.out.println("=".repeat(50) + "\n");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            System.out.println("=".repeat(50) + "\n");
        }
    }

    /**
     * Displays a screen for bulk creating multiple teams.
     * Allows user to specify number of teams and players per team.
     * Teams are auto-generated with names (Team 1, Team 2, etc.) and auto-populated with players.
     */
    private void displayBulkCreateTeams() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("BULK CREATE TEAMS");
        System.out.println("=".repeat(50));
        
        // Get number of teams
        System.out.print("How many teams would you like to create? ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            return;
        }
        int numTeams = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (numTeams <= 0) {
            System.out.println("Number of teams must be greater than 0.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        // Get number of players per team
        System.out.print("How many players per team? ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            return;
        }
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (numPlayers <= 0) {
            System.out.println("Number of players must be greater than 0.");
            System.out.println("=".repeat(50) + "\n");
            return;
        }
        
        // Create teams
        int successCount = 0;
        java.util.Set<String> existingNames = manager.getTeams().keySet();
        int counter = 1;
        
        System.out.println("\nCreating " + numTeams + " teams with " + numPlayers + " players each...\n");
        
        for (int i = 0; i < numTeams; i++) {
            // Find the next available team name by skipping any that already exist
            while (existingNames.contains("Team " + counter)) {
                counter++;
            }
            String teamName = "Team " + counter++;
            
            try {
                manager.CreateTeam(teamName);
                System.out.println("Created: " + teamName);
                
                // Add players to the team
                for (int j = 1; j <= numPlayers; j++) {
                    String playerName = "Player " + j;
                    try {
                        manager.addPlayerToTeam(teamName, playerName);
                    } catch (IllegalStateException e) {
                        System.out.println("  Warning: Could not add " + playerName + " - " + e.getMessage());
                        break;
                    }
                }
                
                successCount++;
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error creating " + teamName + ": " + e.getMessage());
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Bulk Team Creation Summary:");
        System.out.println("  Successfully created: " + successCount + " teams");
        System.out.println("=".repeat(50) + "\n");
    }

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
