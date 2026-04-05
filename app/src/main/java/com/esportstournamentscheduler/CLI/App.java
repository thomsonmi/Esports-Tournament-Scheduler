package com.esportstournamentscheduler.CLI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.esportstournamentscheduler.domain.model.Team;


public class App {
    private Scanner scanner = new Scanner(System.in);

    public void displayMenu() {
        System.out.println("Welcome to the Esports Tournament Scheduler!");
        System.out.println("Please select an option:");
        System.out.println("1. Create a new tournament");
        System.out.println("2. View existing tournaments");
        System.out.println("3. Create Teams");
        System.out.println("5. Exit");
        
        List<Team> teams = new ArrayList<>();


        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> System.out.println("Creating a new tournament...");
            case 2 -> System.out.println("Viewing existing tournaments...");
            case 3 -> System.out.println("Exiting the application. Goodbye!");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }
    

    public static void main(String[] args) {
        App app = new App();
        app.displayMenu();
    }
}
