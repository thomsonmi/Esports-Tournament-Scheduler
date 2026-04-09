package com.esportstournamentscheduler.CLI;

import java.util.List;
import java.util.Scanner;

/**
 * A simple menu selector that displays options and lets user select via number input.
 * Enhanced with ANSI colors for better visual appeal.
 */
public class SimpleMenuSelector {
    private List<String> options;
    private Scanner scanner;
    
    // ANSI Color codes
    private static final String RESET = "\u001B[0m";
    private static final String BRIGHT_CYAN = "\u001B[96m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String DARK_GRAY = "\u001B[90m";
    private static final String WHITE = "\u001B[97m";
    private String menuHeading;
    
    public SimpleMenuSelector(List<String> options, Scanner scanner, String menuHeading) {
        this.options = options;
        this.scanner = scanner;
        this.menuHeading = menuHeading != null ? menuHeading : "MAIN MENU";
    }
    
    /**
     * Display menu and get user selection.
     * @return the index of the selected option
     */
    public int selectOption() {
        while (true) {
            drawMenu();
            
            System.out.print("\n" + BRIGHT_YELLOW + "  Select an option: " + RESET);
            System.out.print(DARK_GRAY + "> " + RESET);
            
            String input = scanner.nextLine().trim();
            
            try {
                int choice = Integer.parseInt(input) - 1;
                if (choice >= 0 && choice < options.size()) 
                    {
                    clearScreen();
                    return choice;
                } 
                else 
                {
                    System.out.println(BRIGHT_RED + "  ✗ Invalid choice. Enter a number between 1 and " + options.size() + "." + RESET);
                }
            } catch (NumberFormatException e) 
            {
                System.out.println(BRIGHT_RED + "  ✗ Invalid input. Please enter a number." + RESET);
            }
        }
    }
    
    /**
     * Draw the menu on screen with colors and formatting.
     */
    private void drawMenu() {
        clearScreen();
        
        // Header
        System.out.println();
        System.out.println(BRIGHT_CYAN + "=========================================" + RESET);
        System.out.println(BRIGHT_CYAN + "     ESPORTS TOURNAMENT SCHEDULER      " +  RESET);
        System.out.println(BRIGHT_CYAN + "=========================================" + RESET);
        System.out.println();
        
        // Menu title
        System.out.println(BRIGHT_YELLOW + "  " + menuHeading + RESET);
        System.out.println();
        
        // Menu options
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            System.out.println(DARK_GRAY + "      " + RESET 
                + BRIGHT_CYAN + "[" + (i+1) + "]" + RESET 
                + " " + WHITE + option + RESET);
        }
        
        System.out.println();
    }
    
    /**
     * Clear the screen.
     */
    private void clearScreen() {
        try {
            // Works on Windows 10+ PowerShell and Unix terminals
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Fallback - just use ANSI escape
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
}

