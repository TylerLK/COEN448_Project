package com.robot.COEN448_Project;

// Importing Pre-Built Libraries
import java.util.Scanner;
import java.util.Queue;
import java.util.ArrayDeque;

/**
 * App Class
 * 
 * @author Tyler, Sunil
 * 
 * @SHOULD_PRINT_MENU Boolean to control whether or not the menu should be printed during runtime.
 * @floor Entries represent the individual tiles of the floor that the robot will roam around. "0" = Un-drawn and "1" = Drawn.
 * @robot The robot that will be traversing the floor.
 * @isRunning Boolean value to check if the user has quit the program.
 * @commandHistory Queue to keep track of the history of user command input.
 */
public class App {
	private final static boolean SHOULD_PRINT_MENU = true;
	private static int[][] floor;
	private static Robot robot;
	private static boolean isRunning = true;
	private static Queue<String> commandHistory;

	public static void main(String[] args) {
		// Create the scanner to take user input throughout system operation.
		Scanner scanner = new Scanner(System.in);

		// Request initial floor size from user.
		int floorSize;
		while (true) {
			System.out.println("Please enter the size you would like the floor to be (N x N): ");
			String input = scanner.nextLine().trim();

			try {
				floorSize = Integer.parseInt(input);
				if (floorSize > 0) {
					break;
				} else {
					System.out.println("Invalid input. N must be an integer greater than 0.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a whole number (e.g., 5).");
			}
		}

		// Create the initial floor state based on user input.
		initialize(floorSize);
		commandHistory.add("I " + floorSize);

		// Enter a continuous loop so long as the user does not choose to quit the
		// program.
		while (isRunning) {
			// Print the menu for the user.
			if (SHOULD_PRINT_MENU) {
				printMenu();
			}

			// Take the user's command input.
			System.out.println("Enter command: ");
			String command = scanner.nextLine();

			// Execute the user's desired command.
			executeCommand(command, true);
		}
		scanner.close();
	}

	// Program Functions
	/**
	 * [P|p] ==> Print the N X N array and display the indices.
	 * <br><br>
	 * Prints the floor (2D array) to the console.
	 */
	public static void print() {
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
	 * [Q|q] ==> Stop the Program.
	 * <br><br>
	 * Breaks the main program loop and notifies the user of program termination.
	 */
	public static void quit() {
		isRunning = false;
		System.out.println("Exiting Program. Goodbye!");
	}

	/**
	 * [I n|i n] ==> Initialize the System
	 * <br><br>
	 * (Re)Initializes the floor, robot, and command history queue.
	 * <br><br>
	 * Note that the command history queue is only created if it does not already exist.
	 * <br><br>
	 * @param n The size of the floor (N x N).
	 */
	public static void initialize(int n) {
		// Dynamically create the floor array, robot, and history queue.
		floor = new int[n][n];
		robot = new Robot();
		if (commandHistory == null) {
			commandHistory = new ArrayDeque<String>();
		}
	}

	/**
	 * [H|h] ==> Replay all the steps in the history since the last program start.
	 * <br><br>
	 * Re-executes all commands in commandHistory in the order they were input by the user.
	 */
	public static void history() {
		// Loop through the entirety of commandHistory.
		for (int i = 0; i < commandHistory.size(); i++) {
			// Retrieve the next command from the command history.
			String command = commandHistory.poll();

			// Execute the retrieved command.
			executeCommand(command, false);

			// Restore the retrieved command.
			commandHistory.add(command);
		}

		// Notify user of command completion.
		System.out.println("End of Command History.");
	}

	// Utility Functions
	/**
	 * Prints a menu containing all commands available to the user, including their descriptions.
	 */
	public static void printMenu() {
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

	/**
	 * Executes the user's desired command.
	 * <br><br>
	 * Note that invalid commands are not executed.
	 * <br><br>
	 * @param command The user's desired command.
	 * @param addToHistory Boolean to control whether or not the command should be added to commandHistory.
	 */
	public static void executeCommand(String command, boolean addToHistory) {
		if (!isValidCommand(command)) {
			return;
		}

		// Parse the user's command into tokens.
		String[] commandTokens = command.trim().split(" ");

		// Ensure that the command is case blind.
		String caseBlindCommand = commandTokens[0].toLowerCase();

		// Ensure that the quit() and history() commands are not added to the command
		// history.
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
				} catch (ArrayIndexOutOfBoundsException e) {
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
	 * Robust user input validation for all available commands.
	 * <br><br>
	 * @param command The user's desired command.
	 */
	public static boolean isValidCommand(String command) {
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

		// We check single commands, then commands with arguments.
		switch (caseBlindCommand) {
			case "u":
			case "d":
			case "r":
			case "l":
			case "p":
			case "c":
			case "q":
			case "h":
				if (commandTokens.length == 1) {
					return true;
				} else {
					System.out.println("Invalid Command. Too many arguments for this command.");
					return false;
				}

			case "m":
				if (commandTokens.length != 2) {
					System.out.println("Invalid Command. Incorrect number of arguments for this command.");
					return false;
				}
				try {
					int s = Integer.parseInt(commandTokens[1]);
					if (s >= 0) {
						return true;
					} else {
						System.out.println("Invalid Command. The distance must be a non-negative integer.");
						return false;
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid Command. The distance must be a non-negative integer.");
					return false;
				}

			case "i":
				if (commandTokens.length != 2) {
					System.out.println("Invalid Command. Incorrect number of arguments for this command.");
					return false;
				}
				try {
					int n = Integer.parseInt(commandTokens[1]);
					if (n > 0) {
						return true;
					} else {
						System.out.println("Invalid Command. The size must be a positive integer.");
						return false;
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid Command. The size must be a positive integer.");
					return false;
				}
			default:
				System.out.println("Invalid Command. Please try again.");
				return false;
		}
	}

}
