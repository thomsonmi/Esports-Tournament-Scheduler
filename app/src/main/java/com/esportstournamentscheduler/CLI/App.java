package com.esportstournamentscheduler.CLI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.esportstournamentscheduler.application.factory.IPlayerFactory;
import com.esportstournamentscheduler.application.factory.ITeamFactory;
import com.esportstournamentscheduler.application.factory.StandardPlayerFactory;
import com.esportstournamentscheduler.application.factory.StandardTeamFactory;
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

        List<String> tournamentMenuOptions = Arrays.asList(
            "Start Tournament",
            "View Tournament Details",
            "Record Match Results",
            "Add Team",
            "Remove Team from Tournament",
            "View Registered Teams",
            "Back to Main Menu"
        );
        while (running) { 
            
            
            SimpleMenuSelector menu = new SimpleMenuSelector(menuOptions, scanner);
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
                        System.out.println("You selected: " + selectedTournament);
                        
                        SimpleMenuSelector tournamentMenu = new SimpleMenuSelector(tournamentMenuOptions, scanner);
                        int tournamentChoice = tournamentMenu.selectOption();
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
                    // List<String> TournamentsList = new ArrayList<>(manager.getTournaments().keySet());

                    // SimpleMenuSelector teamMenu = new SimpleMenuSelector(TournamentsList, scanner);
                    // int teamChoice = teamMenu.selectOption();
                    

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
        java.util.Map<String, Object> tournaments = manager.getTournaments();
        
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
        java.util.Map<String, Object> tournaments = manager.getTournaments();
        
        if (tournaments.isEmpty()) {
            System.out.println("\n=== Select Tournament ===");
            System.out.println("No tournaments have been created yet.");
            System.out.println("===========================\n");
            return null;
        }
        
        List<String> tournamentList = new ArrayList<>(tournaments.keySet());
        SimpleMenuSelector tournamentMenu = new SimpleMenuSelector(tournamentList, scanner);
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
        
        SimpleMenuSelector menu = new SimpleMenuSelector(tournamentOptions, scanner);
        int choice = menu.selectOption();
        
        return choice;
    }

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
