package com.esportstournamentscheduler.CLI;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

import com.esportstournamentscheduler.application.factory.IPlayerFactory;
import com.esportstournamentscheduler.application.factory.ITeamFactory;
import com.esportstournamentscheduler.application.factory.StandardPlayerFactory;
import com.esportstournamentscheduler.application.factory.StandardTeamFactory;
import com.esportstournamentscheduler.domain.model.Team;
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
                "Select Team",
                "View Tournaments",
                "Quit"
            );
            
            SimpleMenuSelector menu = new SimpleMenuSelector(menuOptions, scanner);
            int choice = menu.selectOption();

            switch (choice) {
                case 0:
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
                    break;
                    
                case 1:
                    // System.out.println("Teams:");
                    // for (Team team : manager.getTeams().values()) {
                    //     System.out.println(team);
                    // }
                    break;
                    
                case 2:
                    // System.out.print("Enter team name to remove: ");
                    // String removeTeamName = scanner.nextLine();
                    // try {
                    //     manager.removeTeam(removeTeamName);
                    //     System.out.println("Team removed successfully.");
                    // } catch (IllegalArgumentException e) {
                    //     System.out.println(e.getMessage());
                    // }
                    break;
                    
                case 3:
                    // // THE NEW MEGA-OPTION
                    // System.out.print("Enter a name for the Tournament: ");
                    // String tName = scanner.nextLine();
                    
                    // System.out.print("Enter the Game being played: ");
                    // String gName = scanner.nextLine();
                    
                    // try {
                    //     // 1. Create it and auto-enroll the saved teams
                    //     manager.createTournamentFromSavedTeams(tName, gName);
                        
                    //     // 2. Immediately start it and generate the bracket tree
                    //     manager.startActiveTournament();
                        
                    //     System.out.println("\nTournament officially started! The bracket has been generated.");
                        
                    //     // 3. Print the visual bracket right away so they see the result!
                    //     manager.printVisualBracket();
                        
                    // } catch (IllegalStateException e) {
                    //     System.out.println("Error: " + e.getMessage());
                    // }
                    break;
                    
                case 4:
                    // manager.printVisualBracket();
                    break;
                    
               case 5:
                    // // 1. Print the bracket first so the user can see the Match IDs
                    // manager.printVisualBracket(); 
                    
                    // System.out.print("Enter the Match ID you want to play (Match 1): ");
                    // String matchId = scanner.nextLine();
                    
                    // System.out.print("Enter score for Team 1 (Top/Left): ");
                    // if (!scanner.hasNextInt()) {
                    //     System.out.println("Invalid score.");
                    //     scanner.next(); continue;
                    // }
                    // int score1 = scanner.nextInt();
                    
                    // System.out.print("Enter score for Team 2 (Bottom/Right): ");
                    // if (!scanner.hasNextInt()) {
                    //     System.out.println("Invalid score.");
                    //     scanner.next(); continue;
                    // }
                    // int score2 = scanner.nextInt();
                    // scanner.nextLine(); // consume newline
                    
                    // try {
                    //     manager.resolveMatch(matchId, score1, score2);
                    //     System.out.println("\nMatch recorded successfully! Advancing winner...");
                        
                    //     // 2. Print the bracket again so they can see the winner move forward!
                    //     manager.printVisualBracket(); 
                    // } catch (Exception e) {
                    //     System.out.println("Error: " + e.getMessage());
                    // }
                    break;

                case 6:
                    // running = false;
                    // System.out.println("Exiting application. Goodbye!");
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
