package com.robot.COEN448_Project;

import java.util.Scanner;

public class App {
	// Main application variables. 
	// Entries represent the individual tiles of the floor that the robot will roam around.
	private static char[][] floor;
	// Entries represent the portions of the floor drawn on. "0" = Un-drawn and "1" = Drawn.
	private static int[][] floorStatus;
	// The robot's current x-axis position on the floor.
	private static int robotPositionX;
	// The robot's current y-axis position on the floor.
	private static int robotPositionY;
	
    public static void main(String[] args) {  	
    	// Create the scanner to take user input throughout system operation.
    	Scanner scanner = new Scanner(System.in);
    	
    	// Request initial floor size from user.
    	System.out.println("Please enter the size you would like the floor to be (N x N): ");
    	int floorSize = scanner.nextInt();
    	
    	Initialize(floorSize);
    	
    	floor[1][2] = '*';
    	Print();
        
    	// Close the scanner.
    	scanner.close();
    }
    
    // TODO: Implement a function to draw the floor with an added grid.
    // Status Functions
    // [P|p] ==> Print the N X N array (i.e., Floor).
    public static void Print() {
    	for (int i = 0; i < floor.length; i++) {
			for (int j = 0; j < floor.length; j++) {
				System.out.print(floor[i][j]);
			}
			System.out.println();
		}
    }
    
    // Program Functions    
    // [I n|i n] ==> Initialize the System
    public static void Initialize(int n) {
    	// Dynamically create the floor and floorStatus arrays.
    	floor = new char[n][n];
    	floorStatus = new int[n][n];
    	
    	// Initialize the floor and the status of its tiles.
    	for (int i = 0; i < n; i++) {
    		for(int j = 0; j < n; j++) {
    			floor[i][j] = ' ';
    			floorStatus[i][j] = 0;
    		}
    	}
    	
    	// Initialize the robots position.
    	robotPositionX = 0;
    	robotPositionY = (n - 1);
    }
    
}
