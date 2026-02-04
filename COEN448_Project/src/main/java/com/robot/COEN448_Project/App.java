package com.robot.COEN448_Project;

// Importing Pre-Built Libraries
import java.util.Scanner;
import java.util.Queue;
import java.util.ArrayDeque;

public class App {
	// Main application variables.
	// Entries represent the individual tiles of the floor that the robot will roam
	// around. "0" = Un-drawn and "1" = Drawn.
	private static int[][] floor;

	// The robot that will be traversing the floor.
	private static Robot robot;

	// Boolean value to check if the user has quit the program.
	private static boolean isRunning = true;

	// Queue to keep track of the history of commands inputed by the user.
	private static Queue<String> commandHistory;

	public static void main(String[] args) {
		// Create the scanner to take user input throughout system operation.
		Scanner scanner = new Scanner(System.in);

		// Request initial floor size from user.
		System.out.println("Please enter the size you would like the floor to be (N x N): ");
		int floorSize = scanner.nextInt();

		// Clear the newline created by nextInt().
		scanner.nextLine();

		// Create the initial floor state based on user input.
		initialize(floorSize);
		commandHistory.add("I " + floorSize);

		// Enter a continuous loop so long as the user does not choose to quit the
		// program.
		while (isRunning) {
			//Print the menu for the user.
			printMenu();
			
			// Take the user's command input.
			System.out.println("Enter command: ");
			String command = scanner.nextLine();
			
			// Execute the user's desired command.
			executeCommand(command, true);
		}
		scanner.close();
	}

	// TODO: Implement a function to draw the user's menu.

	// Program Functions

	/**
	 * Prints the floor (2D array) to the console. [P|p] ==> Print the N X N array
	 * (i.e., Floor).
	 */
	public static void print() {
		System.out.println();
		// flip the rows to print the floor correctly.
		for (int j = floor.length - 1; j >= 0; j--) {
			System.out.println();
			System.out.print(j + " ");
			for (int i = 0; i < floor.length; i++) {
				if (floor[i][j] == 0) {
					System.out.print("     ");
				} else {
					System.out.print("  *  ");
				}
			}

			System.out.println();
		}

		System.out.print("  ");
		for (int i = 0; i < floor.length; i++) {
			System.out.print("  " + i + "  ");
		}
		System.out.println();
	}

	// [Q|q] ==> Stop the Program.
	public static void quit() {
		isRunning = false;
		System.out.println("Exiting Program. Goodbye!");
	}

	// [I n|i n] ==> Initialize the System
	public static void initialize(int n) {
		// Dynamically create the floor array, robot, and history queue.
		floor = new int[n][n];
		robot = new Robot();
		if(commandHistory == null) {
			commandHistory = new ArrayDeque<String>();
		}
	}

	// [H|h] ==> Replay all the steps in the history since the last program start.
	public static void history() {
		// Initialize a temporary queue to hold already printed history commands.
		Queue<String> tempQueue = new ArrayDeque<String>();

		while (!commandHistory.isEmpty()) {
			// Retrieve the next command from the command history.
			String tempCommand = commandHistory.poll();

			// Store the retrieved command into a temporary queue.
			tempQueue.add(tempCommand);

			// Execute the retrieved command.
			executeCommand(tempCommand, false);
		}

		// Restore the original command history and notify user of command completion.
		commandHistory = tempQueue;
		System.out.println("End of Command History.");
	}
	
	// Utility Functions
	// Print a menu for the user.
	public static void printMenu() {
		System.out.println("\nAvailable Commands:");
		System.out.println("[U|u] ==> Pen Up");
		System.out.println("[D|d] ==> Pen Down");
		System.out.println("[R|r] ==> Turn Right");
		System.out.println("[L|l] ==> Turn Left");
		System.out.println("[M s|m s] ==> Move Forward s Spaces (s = Non-negative Integer)");
		System.out.println("[P|p] ==> Print the Floor");
		System.out.println("[C|c] ==> Print the Robot's Current Position and Direction");
		System.out.println("[I n|i n] ==> Initialize the System with a New Floor of Size n x n (n = Positive Integer)");
		System.out.println("[H|h] ==> Replay Command History");
		System.out.println("[Q|q] ==> Stop the Program\n");
	}

	// Execute a given command.
	public static void executeCommand(String command, boolean addToHistory) {
		// Parse the user's command into tokens.
		String[] commandTokens = command.trim().split(" ");

		// Ensure that the command is case blind.
		String caseBlindCommand = commandTokens[0].toLowerCase();

		// Execute the user's desired command and update the history queue.
		switch (caseBlindCommand) {
			case "u":
				robot.penUp();
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "d":
				robot.penDown();
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "r":
				robot.turnRight();
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "l":
				robot.turnLeft();
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "m":
				robot.move(Integer.parseInt(commandTokens[1]), floor);
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "p":
				print();
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "c":
				System.out.println(robot);
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "q":
				quit();
				break;
			case "i":
				initialize(Integer.parseInt(commandTokens[1]));
				if(addToHistory) {
					commandHistory.add(command);
				}
				break;
			case "h":
				history();
				break;
			default:
				System.out.println("Invalid Command. Please try again.");
				break;
		}
	}

}
