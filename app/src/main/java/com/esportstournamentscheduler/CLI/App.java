package com.esportstournamentscheduler.CLI;

import java.util.Scanner;

import com.esportstournamentscheduler.application.factory.IPlayerFactory;
import com.esportstournamentscheduler.application.factory.ITeamFactory;
import com.esportstournamentscheduler.application.factory.StandardPlayerFactory;
import com.esportstournamentscheduler.application.factory.StandardTeamFactory;
import com.esportstournamentscheduler.domain.model.Team;
import com.esportstournamentscheduler.manager.TournamentManager;

public class App {
    private Scanner scanner = new Scanner(System.in);
    private TournamentManager manager;

    public App() {
       ITeamFactory teamFactory = new StandardTeamFactory();
       IPlayerFactory playerFactory = new StandardPlayerFactory();
       manager = new TournamentManager(teamFactory, playerFactory);
    }

    public void start() {
        boolean running = true;

        while (running) { 
            System.out.println("1. Create Team");
            System.out.println("2. View Teams");
            System.out.println("3. Remove Team");
            System.out.println("4. Create Tournament");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            if(!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // consume the invalid input
                continue;
            };
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
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
                        
                        
                        for (int i = 0; i < numPlayers; i++) {
                            System.out.print("Enter name for Player " + (i + 1) + ": ");
                            String playerName = scanner.nextLine();
                            
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
                    
                case 2:
                    System.out.println("Teams:");
                    for (Team team : manager.getTeams().values()) {
                        System.out.println(team);
                    }
                    break;
                case 3:
                    System.out.print("Enter team name to remove: ");
                    String removeTeamName = scanner.nextLine();
                    try {
                        manager.removeTeam(removeTeamName);
                        System.out.println("Team removed successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Create Tournament functionality not implemented yet.");
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

        }       
    }
    

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
