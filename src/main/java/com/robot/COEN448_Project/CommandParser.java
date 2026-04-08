package com.robot.COEN448_Project;

import java.util.Set;

/**
 * CommandParser Class
 * 
 * Handles the logic related to parsing and validating user commands.
 */
public class CommandParser {
    private static final Set<String> VALID_SINGLE_TOKEN_COMMANDS = Set.of("u", "d", "r", "l", "p", "c", "q", "h");

    /**
     * Robust user input validation for all available commands.
     * 
     * @param command The user's desired command.
     * @return {@code true} if the command is valid; {@code false} otherwise.
     */
    public boolean isValidCommand(String command) {
        // Check if the command is empty.
        if (command == null || command.trim().isEmpty()) {
            System.out.println("Empty Command. Please try again.");
            return false;
        }

        String[] commandTokens = command.trim().split(" ");

        if (commandTokens.length > 2) {
            System.out.println("Invalid Command. Too many arguments for this command.");
            return false;
        }

        String caseBlindCommand = commandTokens[0].toLowerCase();

        if (isSingleTokenCommand(caseBlindCommand)) {
            return isValidNoArgumentCommand(commandTokens.length);
        }

        switch (caseBlindCommand) {
            case "m":
                return isValidDistanceCommand(commandTokens);
            case "i":
                return isValidInitializeCommand(commandTokens);
            default:
                System.out.println("Invalid Command. Please try again.");
                return false;
        }
    }

    /**
     * Checks if the command is one of the valid single token commands.
     * @param caseBlindCommand The command to check.
     * @return {@code true} if the command is valid; {@code false} otherwise.
     */
    private boolean isSingleTokenCommand(String caseBlindCommand) {
        return VALID_SINGLE_TOKEN_COMMANDS.contains(caseBlindCommand);
    }

    /**
     * Checks if the commandLength is 1, meaning that the command is valid.
     * @param commandLength The length of the command.
     * @return {@code true} if the command is valid; {@code false} otherwise.
     */
    private boolean isValidNoArgumentCommand(int commandLength) {
        if (commandLength == 1) {
            return true;
        }

        System.out.println("Invalid Command. Too many arguments for this command.");
        return false;
    }

    /**
     * Checks if the distance command is valid.
     * @param commandTokens The command tokens.
     * @return {@code true} if the command is valid; {@code false} otherwise.
     */
    private boolean isValidDistanceCommand(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("Invalid Command. Incorrect number of arguments for this command.");
            return false;
        }

        try {
            int s = Integer.parseInt(commandTokens[1]);
            if (s >= 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            // Fall through to shared error message.
        }

        System.out.println("Invalid Command. The distance must be a non-negative integer.");
        return false;
    }

    /**
     * Checks if the initialize command is valid.
     * @param commandTokens The command tokens.
     * @return {@code true} if the command is valid; {@code false} otherwise.
     */
    private boolean isValidInitializeCommand(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("Invalid Command. Incorrect number of arguments for this command.");
            return false;
        }

        try {
            int n = Integer.parseInt(commandTokens[1]);
            if (n > 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            // Fall through to shared error message.
        }

        System.out.println("Invalid Command. The size must be a positive integer.");
        return false;
    }
}
