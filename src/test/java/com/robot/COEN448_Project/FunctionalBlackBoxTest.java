package com.robot.COEN448_Project;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalBlackBoxTest {

    private Robot robot;
    private int[][] standardFloor;
    
    // Used to capture System.out.println for commands like C, P, and H
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Redirect standard output to our stream to test print commands
        System.setOut(new PrintStream(outContent));
        
        // Initialize Robot for direct tests
        robot = new Robot(); 
        standardFloor = new int[5][5]; // 5x5 grid for testing
        
        // Initialize App state for application-level command tests
        App.initialize(5); 
    }

    @AfterEach
    public void restoreStreams() {
        // Restore standard output after each test
        System.setOut(originalOut);
    }

    // ==========================================
    // PEN TOGGLE REQUIREMENTS 
    // ==========================================

    @Test
    public void testPenStateTransitions() {
        // Initial state should be UP
        assertEquals(PenOrientation.UP, robot.getPenOrientation(), "Pen should start UP.");
        
        // Command 'D' lowers pen
        robot.penDown();
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation(), "Pen should be DOWN after penDown().");
        
        // Command 'U' lifts pen
        robot.penUp();
        assertEquals(PenOrientation.UP, robot.getPenOrientation(), "Pen should be UP after penUp().");
    }

    // ==========================================
    // ROTATION REQUIREMENTS 
    // ==========================================

    @Test
    public void testTurnRightCyclesCorrectly() {
        assertEquals(Orientation.NORTH, robot.getDirection()); // Starts North
        
        robot.turnRight();
        assertEquals(Orientation.EAST, robot.getDirection());
        
        robot.turnRight();
        assertEquals(Orientation.SOUTH, robot.getDirection());
        
        robot.turnRight();
        assertEquals(Orientation.WEST, robot.getDirection());
        
        robot.turnRight();
        assertEquals(Orientation.NORTH, robot.getDirection()); // Full circle
    }

    @Test
    public void testTurnLeftCyclesCorrectly() {
        assertEquals(Orientation.NORTH, robot.getDirection()); // Starts North
        
        robot.turnLeft();
        assertEquals(Orientation.WEST, robot.getDirection());
        
        robot.turnLeft();
        assertEquals(Orientation.SOUTH, robot.getDirection());
        
        robot.turnLeft();
        assertEquals(Orientation.EAST, robot.getDirection());
        
        robot.turnLeft();
        assertEquals(Orientation.NORTH, robot.getDirection()); // Full circle
    }

    // ==========================================
    // MOVEMENT & DRAWING REQUIREMENTS
    // ==========================================

    @Test
    public void testMoveWithPenUpDoesNotMarkFloor() {
        robot.move(3, standardFloor);
        
        assertEquals(0, robot.getX(), "X should remain 0");
        assertEquals(3, robot.getY(), "Y should be 3 after moving NORTH");

        // Verify floor is completely unmarked
        for (int i = 0; i <= 3; i++) {
            assertEquals(0, standardFloor[0][i], "Floor should remain 0 when pen is UP.");
        }
    }

    @Test
    public void testMoveWithPenDownMarksPath() {
        robot.penDown();
        robot.move(2, standardFloor); // Move to [0,2]
        
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());

        // Verify the path was drawn
        assertEquals(1, standardFloor[0][0], "Origin should be marked 1.");
        assertEquals(1, standardFloor[0][1], "Path tile [0,1] should be marked 1.");
        assertEquals(1, standardFloor[0][2], "Destination tile [0,2] should be marked 1.");
        
        assertEquals(0, standardFloor[0][3], "Tiles beyond movement should remain 0.");
    }

    // ==========================================
    // APPLICATION COMMAND REQUIREMENTS (C, I, P, H)
    // ==========================================

    @Test
    public void testPrintPositionCommand() {
        // Move the robot using app commands
        App.executeCommand("R", true); // Face East
        App.executeCommand("M 2", true); // Move 2 spaces
        
        outContent.reset(); // Clear console log
        
        // Execute [C|c] command to print current position 
        App.executeCommand("C", true);
        
        String output = outContent.toString();
        assertTrue(output.contains("Position: 2, 0 - Pen: UP - Facing: EAST"), 
            "The C command should print the updated coordinates, pen state, and direction.");
    }

    @Test
    public void testInitializeCommandResetsSystem() {
        // Mess up the initial state
        App.executeCommand("D", true);
        App.executeCommand("R", true);
        App.executeCommand("M 4", true);
        
        // Execute [I n|i n] command 
        App.executeCommand("I 8", true);
        
        outContent.reset();
        App.executeCommand("C", true); 
        String output = outContent.toString();
        
        // Verify everything went back to defaults 
        assertTrue(output.contains("Position: 0, 0 - Pen: UP - Facing: NORTH"), 
            "The I command should reset the robot to [0,0], UP, and NORTH.");
    }

    @Test
    public void testPrintFloorCommand() {
        App.executeCommand("D", true);
        App.executeCommand("M 2", true);
        
        outContent.reset();
        
        // Execute [P|p] command 
        App.executeCommand("P", true);
        
        String output = outContent.toString();
        // Since the array formatting is dynamic, we check for the presence of the asterisk
        // that represents a marked tile.
        assertTrue(output.contains("*"), "The P command should print the floor and display asterisks for marked tiles.");
    }

    @Test
    public void testHistoryCommandReplaysSteps() {
        App.executeCommand("I 5", true); // Initializes history
        App.executeCommand("R", true);
        App.executeCommand("M 1", true);
        
        outContent.reset();
        
        // Execute [H|h] command 
        App.executeCommand("H", true);
        
        String output = outContent.toString();
        // Check that history announces its completion
        assertTrue(output.contains("End of Command History."), 
            "The H command should replay all steps and print the completion message.");
    }
}