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
        while (running) { 
            // Use new simple menu selector
            List<String> menuOptions = Arrays.asList(
                "Create Tournament",
                "Select Tournament",
                "Create Team",
                "View All Teams",
                "View Tournaments",
                "Quit"
            );
            
            SimpleMenuSelector menu = new SimpleMenuSelector(menuOptions, scanner);
            int choice = menu.selectOption();

            switch (choice) {
                case 0: // Create Tournament
                    System.out.print("Enter a name for the Tournament: ");
                    String tournamentName = scanner.nextLine();
                    System.out.print("Which game is being played? ");
                    String gameName = scanner.nextLine();
                    System.out.print("How many teams will be participating? (4 or 8) ");
                    String numberOfTeams = scanner.nextLine();

                    try 
                    {
                        manager.CreateTournament(tournamentName, gameName, Integer.parseInt(numberOfTeams));
                        System.out.println("Tournament '" + tournamentName + "' for game '" + gameName + "' has been created successfully.");
                    } catch (IllegalArgumentException e) 
                    {
                        // Incorrect number of teams
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                    // System.out.print("Enter the name of the new team: ");
                    // String teamName = scanner.nextLine();
                    // try {
                    //     manager.CreateTeam(teamName);
                    //     System.out.println("Success! Team '" + teamName + "' has been created.");
                        
                    //     System.out.print("How many players are on your team? ");
                    //     if (!scanner.hasNextInt()) {
                    //         System.out.println("Invalid input. Please enter a number.");
                    //         scanner.next(); 
                    //         continue; 
                    //     }
                        
                    //     int numPlayers = scanner.nextInt();
                    //     scanner.nextLine(); // Consume newline
                        
                    //     for (int i = 0; i < numPlayers; i++) {
                    //         System.out.print("Enter name for Player " + (i + 1) + ": ");
                    //         String playerName = scanner.nextLine();
                            
                    //         try {
                    //             manager.addPlayerToTeam(teamName, playerName);
                    //             System.out.println("  -> Added '" + playerName + "' to '" + teamName + "'.");
                    //         } catch (IllegalStateException e) {
                    //             System.out.println("  -> Error: " + e.getMessage());
                    //             System.out.println("  -> Stopping player additions for this team.");
                    //             break; 
                    //         }
                    //     }
                    //     System.out.println("Team setup complete!");
                        
                    // } catch (IllegalArgumentException e) {
                    //     System.out.println("Error: " + e.getMessage());
                    // }
                    
                case 1: // Select Tournament
                    // System.out.println("Teams:");
                    // for (Team team : manager.getTeams().values()) {
                    //     System.out.println(team);
                    // }
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
                    List<String> TournamentsList = new ArrayList<>(manager.getTournaments().keySet());

                    SimpleMenuSelector teamMenu = new SimpleMenuSelector(TournamentsList, scanner);
                    int teamChoice = teamMenu.selectOption();
                    //print bracket of selected team
                    //[TODO]

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
    

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
