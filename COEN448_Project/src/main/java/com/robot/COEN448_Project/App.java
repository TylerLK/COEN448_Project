package com.robot.COEN448_Project;

// Importing Pre-Built Libraries
import java.util.Scanner;

// Importing User-Defined Enumerations
public class App {
	// Main application variables. 
	// Entries represent the individual tiles of the floor that the robot will roam around. "0" = Un-drawn and "1" = Drawn.
	private static int[][] floor;
	
	// The robot that will be traversing the floor.
	private static Robot robot;
	
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
    	
    	// Enter a continuous loop so long as the user does not choose to quit the program.
    	while(true) {
    		// Take the user's command input.
    		System.out.println("Enter command: ");
    		String command = scanner.nextLine();
    		
    		
    		// Parse the user's command into tokens.
    		String[] commandTokens = command.trim().split(" ");
    		
    		// Ensure that the command is case blind.
    		String caseBlindCommand = commandTokens[0].toLowerCase();
    		
    		switch (caseBlindCommand) {
    			case "p":
    				print();
    				break;
    			case "i":
    				initialize(Integer.parseInt(commandTokens[1]));
    				break;
    			case "q":
    				System.out.println("Exiting Program. Goodbye!");
    				scanner.close();
    				System.exit(0);
    			default:
    				System.out.println("Invalid Command. Please try again.");
    				break;
    		}
    	}
    }
    
    // TODO: Implement a function to draw the floor with an added grid.
    // Status Functions
    // [P|p] ==> Print the N X N array (i.e., Floor).
    public static void print() {
		/*
		 * for (int i = 0; i < floor.length; i++) { for (int j = 0; j < floor.length;
		 * j++) { System.out.print(floor[i][j]); } System.out.println(); }
		 */
    	System.out.println("Print function called.");
    }
    
    // Program Functions    
    // [I n|i n] ==> Initialize the System
    public static void initialize(int n) {
    	// Dynamically create the floor array and robot.
    	floor = new int[n][n];
    	robot = new Robot();
    	
    	// Initialize the floor and the status of its tiles.
    	for (int i = 0; i < n; i++) {
    		for(int j = 0; j < n; j++) {
    			floor[i][j] = 0;
    		}
    	}
    }
    
}
