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
import com.esportstournamentscheduler.manager.TournamentManager;

public class App {
    private Scanner scanner = new Scanner(System.in);
    private TournamentManager manager;

    public App() {
       ITeamFactory teamFactory = new StandardTeamFactory();
       IPlayerFactory playerFactory = new StandardPlayerFactory();
         ITeamValidationPolicy teamValidationPolicy = new FlexibleTeamValidationPolicy();
         manager = new TournamentManager(teamFactory, playerFactory, teamValidationPolicy);
    }
    

public void start() {
        boolean running = true;
        // PRE-POPULATE SOME TEAMS & PLAYERS FOR DEMO PURPOSES
    // manager.CreateTeam("Alpha");
    // manager.addPlayerToTeam("Alpha", "A1");
    // manager.addPlayerToTeam("Alpha", "A2");

    // manager.CreateTeam("Bravo");
    // manager.addPlayerToTeam("Bravo", "B1");
    // manager.addPlayerToTeam("Bravo", "B2");

    // manager.CreateTeam("Crimson");
    // manager.addPlayerToTeam("Crimson", "C1");
    // manager.addPlayerToTeam("Crimson", "C2");

    // manager.CreateTeam("Delta");
    // manager.addPlayerToTeam("Delta", "D1");
    // manager.addPlayerToTeam("Delta", "D2");
/* 
    manager.CreateTeam("Echo");
    manager.addPlayerToTeam("Echo", "E1");
    manager.addPlayerToTeam("Echo", "E2");

    manager.CreateTeam("Foxtrot");
    manager.addPlayerToTeam("Foxtrot", "F1");
    manager.addPlayerToTeam("Foxtrot", "F2");

    manager.CreateTeam("Gamma");
    manager.addPlayerToTeam("Gamma", "G1");
    manager.addPlayerToTeam("Gamma", "G2");

    manager.CreateTeam("Hotel");
    manager.addPlayerToTeam("Hotel", "H1");
    manager.addPlayerToTeam("Hotel", "H2");
*/
    // Use new simple menu selector
        List<String> menuOptions = Arrays.asList(
            "Create Tournament",
            "Select Tournament",
            "Create Team",
            "View All Teams",
            "View Tournaments",
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
                        manager.CreateTournament(tournamentData[0], tournamentData[1], Integer.parseInt(tournamentData[2]));
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
                        while(tournamentChoice != 6) 
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
                                    // logic here
                                    break;
                                case 3: // Add team to tournament (register team)
                                    break;
                                case 4: // Remove team from tournament
                                    break;
                                case 5: // Start Tournament
                                    try {
                                        manager.setSelectedTournament(selectedTournament);
                                        manager.startActiveTournament();
                                        System.out.println("Tournament '" + selectedTournament + "' has been started.");
                                    } catch (IllegalStateException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;
                                case 6: // Back to Main Menu
                                    // automatically returns to main menu
                                    break;
                                case 8: // Display Bracket
                                    displayTournamentBracket(selectedTournament);
                                    break;
                                default:
                                    System.out.println("Invalid option. Please select a valid menu item.");
                            }
                        }
                    }

                    break;
                    
                case 2:// Create Team
                    System.out.print("Enter the name of the new team: ");
                    String teamName = scanner.nextLine();
                    try {
                        manager.CreateTeam(teamName);
                        System.out.println("Success! Team '" + teamName + "' has been created.");
                        
                        System.out.print("How many players are on your team? ");
                        if (!scanner.hasNextInt()) {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.next(); 
                            continue; 
                        }
                        
                        int numPlayers = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        
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
                case 3: // View All Teams
                    manager.printAllTeams();
                    break;
                    
                case 4: // View Tournaments
                // convert map to list of team names for menu selector
                    displayExistingTournaments();                   

                    break;

                case 5: // Quit
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
            }
            
            if (running && choice != 0) {
                System.out.print("Press ENTER to continue...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Prompts the user for tournament creation details.
     * @return A String array containing [tournamentName, gameName, numberOfTeams]
     */
    private String[] promptForTournamentCreation() {
        System.out.println("\n=== Create New Tournament ===");
        
        System.out.print("Enter a name for the Tournament: ");
        String tournamentName = scanner.nextLine();
        
        System.out.print("Which game is being played? ");
        String gameName = scanner.nextLine();
        
        System.out.print("How many teams will be participating? (4 or 8): ");
        String numberOfTeams = scanner.nextLine();
        
        System.out.println("=============================\n");
        
        return new String[]{tournamentName, gameName, numberOfTeams};
    }
    
    /**
     * Displays all existing tournaments in a formatted view.
     */
    private void displayExistingTournaments() {
        java.util.Map<String, Tournament> tournaments = manager.getTournaments();
        
        System.out.println("\n=== Existing Tournaments ===");
        
        if (tournaments.isEmpty()) {
            System.out.println("No tournaments have been created yet.");
        } else {
            int index = 1;
            for (String tournamentName : tournaments.keySet()) {
                System.out.println(index + ". " + tournamentName);
                index++;
            }
        }
        
        System.out.println("=============================\n");
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
        
        List<String> tournamentList = new ArrayList<>(tournaments.keySet());
        SimpleMenuSelector tournamentMenu = new SimpleMenuSelector(tournamentList, scanner, "SELECT TOURNAMENT");
        int selectedIndex = tournamentMenu.selectOption();
        
        return tournamentList.get(selectedIndex);
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
            "Start Tournament",
            "Record Match Results",
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

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
