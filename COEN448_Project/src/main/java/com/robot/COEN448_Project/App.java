package com.robot.COEN448_Project;

// Importing Pre-Built Libraries
import java.util.Scanner;

public class App {
	// Main application variables.
	// Entries represent the individual tiles of the floor that the robot will roam
	// around. "0" = Un-drawn and "1" = Drawn.
	private static int[][] floor;

	// The robot that will be traversing the floor.
	private static Robot robot;

	// Boolean value to check if the user has quit the program.
	private static boolean isRunning = true;

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

		// Enter a continuous loop so long as the user does not choose to quit the
		// program.
		while (isRunning) {
			// Take the user's command input.
			System.out.println("Enter command: ");
			String command = scanner.nextLine();

			// Parse the user's command into tokens.
			String[] commandTokens = command.trim().split(" ");

			// Ensure that the command is case blind.
			String caseBlindCommand = commandTokens[0].toLowerCase();

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
				robot.move(Integer.parseInt(commandTokens[1]), floor);
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
		scanner.close();
	}

	// TODO: Implement a function to draw the floor with an added grid.
	// TODO: Implement a function to draw the user's menu.

	// Program Functions
	// [P|p] ==> Print the N X N array (i.e., Floor).
	public static void print() {
		/*
		 * for (int i = 0; i < floor.length; i++) { for (int j = 0; j < floor.length;
		 * j++) { System.out.print(floor[i][j]); } System.out.println(); }
		 */
		System.out.println("Print function called.");
	}

	// [Q|q] ==> Stop the Program.
	public static void quit() {
		isRunning = false;
		System.out.println("Exiting Program. Goodbye!");
	}

	// [I n|i n] ==> Initialize the System
	public static void initialize(int n) {
		// Dynamically create the floor array and robot.
		floor = new int[n][n];
		robot = new Robot();
	}
	
	// [H|h] ==> Replay all the steps in the history since the last program start.
	public static void history() {
		System.out.println("History function called.");
	}

}
