package com.robot.COEN448_Project;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * CommandExecutor Class
 * 
 * Handles the state and execution logic for the robot simulation.
 */
public class CommandExecutor {
    private int[][] floor;
    private Robot robot;
    private boolean isRunning = true;
    private Queue<String> commandHistory;
    private final CommandParser parser;

    public CommandExecutor(CommandParser parser) {
        this.parser = parser;
        this.commandHistory = new ArrayDeque<>();
    }

    /**
     * Executes the user's desired command.
     * 
     * @param command The user's desired command.
     * @param addToHistory Boolean to control whether or not the command should be added to commandHistory.
     */
    public void executeCommand(String command, boolean addToHistory) {
        if (!parser.isValidCommand(command)) {
            return;
        }

        // Parse the user's command into tokens.
        String[] commandTokens = command.trim().split(" ");

        // Ensure that the command is case blind.
        String caseBlindCommand = commandTokens[0].toLowerCase();

        // Ensure that the quit() and history() commands are not added to the command history.
        boolean shouldAddToHistory = addToHistory && !caseBlindCommand.equals("q") && !caseBlindCommand.equals("h");
        if (shouldAddToHistory) {
            commandHistory.add(command);
        }

        // Execute the user's desired command and update the history queue.
        switch (caseBlindCommand) {
            case "u":
                robot.penUp();
                break;
            case "d":
                robot.penDown();
                break;
            case "r":
                robot.turnRight();
                break;
            case "l":
                robot.turnLeft();
                break;
            case "m":
                try {
                    robot.move(Integer.parseInt(commandTokens[1]), floor);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "p":
                print();
                break;
            case "c":
                System.out.println(robot);
                break;
            case "q":
                quit();
                break;
            case "i":
                initialize(Integer.parseInt(commandTokens[1]));
                break;
            case "h":
                history();
                break;
            default:
                System.out.println("Invalid Command. Please try again.");
                break;
        }
    }

    /**
     * [I n|i n] ==> Initialize the System
     * 
     * (Re)Initializes the floor and robot.
     * 
     * @param n The size of the floor (N x N).
     */
    public void initialize(int n) {
        floor = new int[n][n];
        robot = new Robot();
    }

    /**
     * [P|p] ==> Print the N X N array and display the indices.
     */
    public void print() {
        System.out.println();

        // Create spacing dynamically based on the size of the floor.
        int maxDigits = String.valueOf(floor.length - 1).length();
        String format = "%" + (maxDigits + 3) + "s";

        // Flip the rows to print the floor correctly.
        for (int j = floor.length - 1; j >= 0; j--) {
            System.out.println();

            // Format the row with a dynamic width.
            System.out.printf("%" + maxDigits + "d ", j);

            // Print out the floor tiles with the dynamic spacing.
            for (int i = 0; i < floor.length; i++) {
                if (floor[i][j] == 0) {
                    System.out.printf(format, " ");
                } else {
                    System.out.printf(format, "*");
                }
            }

            System.out.println();
        }

        // Print the bottom index with dynamic spacing.
        System.out.print(String.format("%" + maxDigits + "s", " ") + " ");
        for (int i = 0; i < floor.length; i++) {
            System.out.printf(format, i);
        }
        System.out.println();
    }

    /**
     * [H|h] ==> Replay all the steps in the history since the last program start.
     */
    public void history() {
        int historySize = commandHistory.size();
        for (int i = 0; i < historySize; i++) {
            String command = commandHistory.poll();
            executeCommand(command, false);
            commandHistory.add(command);
        }
        System.out.println("End of Command History.");
    }

    /**
     * [Q|q] ==> Stop the Program.
     */
    public void quit() {
        isRunning = false;
        System.out.println("Exiting Program. Goodbye!");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void addToHistory(String command) {
        commandHistory.add(command);
    }
}
