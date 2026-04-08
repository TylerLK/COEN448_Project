package com.robot.COEN448_Project;

import java.util.Scanner;

/**
 * SimulationController Class
 * 
 * Manages the main program loop, user input, and orchestrates command parsing and execution.
 */
public class SimulationController {
    private static final boolean SHOULD_PRINT_MENU = true;
    private final CommandParser parser;
    private final CommandExecutor executor;
    private final Scanner scanner;

    public SimulationController() {
        this.parser = new CommandParser();
        this.executor = new CommandExecutor(this.parser);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the simulation process.
     */
    public void run() {
        int floorSize = requestInitialFloorSize();
        executor.initialize(floorSize);
        executor.addToHistory("I " + floorSize);

        while (executor.isRunning()) {
            if (SHOULD_PRINT_MENU) {
                printMenu();
            }

            System.out.println("Enter command: ");
            String command = scanner.nextLine();
            executor.executeCommand(command, true);
        }
        scanner.close();
    }

    /**
     * Requests the initial floor size from the user.
     * 
     * @return The validated floor size.
     */
    private int requestInitialFloorSize() {
        int floorSize;
        while (true) {
            System.out.println("Please enter the size you would like the floor to be (N x N): ");
            String input = scanner.nextLine().trim();

            try {
                floorSize = Integer.parseInt(input);
                if (floorSize > 0) {
                    return floorSize;
                } else {
                    System.out.println("Invalid input. N must be an integer greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number (e.g., 5).");
            }
        }
    }

    /**
     * Prints the available commands menu.
     */
    private void printMenu() {
        System.out.println("\nAvailable Commands:");
        System.out.println("[U|u]      |  Pen Up");
        System.out.println("[D|d]      |  Pen Down");
        System.out.println("[R|r]      |  Turn Right");
        System.out.println("[L|l]      |  Turn Left");
        System.out.println("[M s|m s]  |  Move Forward s Spaces (s = Non-negative Integer)");
        System.out.println("[P|p]      |  Print the Floor");
        System.out.println("[C|c]      |  Print the Robot's Current Position and Direction");
        System.out.println("[I n|i n]  |  Initialize the System with a New Floor of Size n x n (n = Positive Integer)");
        System.out.println("[H|h]      |  Replay Command History");
        System.out.println("[Q|q]      |  Stop the Program\n");
    }
}
