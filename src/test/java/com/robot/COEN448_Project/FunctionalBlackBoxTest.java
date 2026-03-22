package com.robot.COEN448_Project;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionalBlackBoxTest {

    private Robot robot;
    private int[][] standardFloor;

    @BeforeEach
    public void setUp() {
        // R2: Initialize at [0,0], Pen UP, facing NORTH
        robot = new Robot(); 
        standardFloor = new int[5][5]; // 5x5 grid for testing
    }

    // ==========================================
    // PEN TOGGLE REQUIREMENTS (R3, R4)
    // ==========================================

    @Test
    public void testPenStateTransitions() {
        // Initial state should be UP (R2)
        assertEquals(PenOrientation.UP, robot.getPenOrientation(), "Pen should start UP.");
        
        // R4: Command 'D' lowers pen
        robot.penDown();
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation(), "Pen should be DOWN after penDown().");
        
        // R3: Command 'U' lifts pen
        robot.penUp();
        assertEquals(PenOrientation.UP, robot.getPenOrientation(), "Pen should be UP after penUp().");
    }

    // ==========================================
    // ROTATION REQUIREMENTS (R8, R9)
    // ==========================================

    @Test
    public void testTurnRightCyclesCorrectly() {
        // R8: Turn 90 degrees right
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
        // R9: Turn 90 degrees left
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
    // MOVEMENT & DRAWING REQUIREMENTS (R5, R6, R7)
    // ==========================================

    @Test
    public void testMoveWithPenUpDoesNotMarkFloor() {
        // R7: Pen is UP (default). Moving should not change floor values.
        robot.move(3, standardFloor);
        
        // Verify position changed (R5)
        assertEquals(0, robot.getX(), "X should remain 0");
        assertEquals(3, robot.getY(), "Y should be 3 after moving NORTH");

        // Verify floor is completely unmarked
        for (int i = 0; i <= 3; i++) {
            assertEquals(0, standardFloor[0][i], "Floor should remain 0 when pen is UP.");
        }
    }

    @Test
    public void testMoveWithPenDownMarksPath() {
        // R6: Pen is DOWN. Moving should mark cells with 1.
        robot.penDown();
        robot.move(2, standardFloor); // Move to [0,2]
        
        // Verify position changed
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());

        // Verify the path was drawn, including the starting square!
        assertEquals(1, standardFloor[0][0], "Origin should be marked 1.");
        assertEquals(1, standardFloor[0][1], "Path tile [0,1] should be marked 1.");
        assertEquals(1, standardFloor[0][2], "Destination tile [0,2] should be marked 1.");
        
        // Verify the rest of the floor is still 0
        assertEquals(0, standardFloor[0][3], "Tiles beyond movement should remain 0.");
    }
    
    @Test
    public void testMultiDirectionalDrawing() {
        // Complex Black-box test combining multiple requirements
        robot.penDown();
        robot.move(2, standardFloor); // Draw North to [0,2]
        robot.turnRight();            // Face East
        robot.move(2, standardFloor); // Draw East to [2,2]
        
        assertEquals(2, robot.getX());
        assertEquals(2, robot.getY());
        
        // Verify the L-shape was drawn correctly
        assertEquals(1, standardFloor[0][0]);
        assertEquals(1, standardFloor[0][1]);
        assertEquals(1, standardFloor[0][2]); // Corner
        assertEquals(1, standardFloor[1][2]);
        assertEquals(1, standardFloor[2][2]); // End point
    }
}