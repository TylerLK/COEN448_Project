package com.robot.COEN448_Project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoundaryValidationTest {

    private Robot robot;
    private int[][] standardFloor;

    @BeforeEach
    public void setUp() {
        // Resets the robot and floor before every single test
        robot = new Robot(); // Initial state: 0,0, facing NORTH, pen UP
        standardFloor = new int[10][10];
    }

    // ==========================================
    // 1. MOVEMENT BOUNDARIES (Robot.java)
    // ==========================================

    @Test
    public void testMoveStopsAtGridBoundary() {
        // Setup: Robot is at 0,0 facing NORTH.
        // Action: Try to move 15 steps on a 10x10 grid (maximum Y index is 9).
        robot.move(15, standardFloor);

        // Assert: The refactored logic should stop at 0,9 instead of throwing an exception.
        assertEquals(0, robot.getX(), "X position should remain 0.");
        assertEquals(9, robot.getY(), "Y position should cap at the maximum grid index (9).");
    }

    @Test
    public void testMoveZeroStepsWithPenDown() {
        // Setup: Pen down, robot at origin.
        robot.penDown();
        
        // Action: Command the robot to move 0 steps.
        robot.move(0, standardFloor);

        // Assert: Position should not change.
        assertEquals(0, robot.getX(), "X should remain 0");
        assertEquals(0, robot.getY(), "Y should remain 0");
        
        // Assert: The current code's loop `for (int i = 0; i < steps; i++)` bypasses marking
        // the floor if steps == 0. This verifies that behavior.
        assertEquals(0, standardFloor[0][0], "Floor at 0,0 should remain 0 because the loop does not execute for 0 steps.");
    }

    @Test
    public void testMoveNegativeStepsThrowsException() {
        // Assert: Moving negative steps directly via the robot method should throw an exception.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            robot.move(-3, standardFloor);
        });
        assertEquals("Steps must be a non-negative integer.", exception.getMessage());
    }

    @Test
    public void testMoveOnMinimumValidGrid() {
        // Setup: Create the smallest possible valid grid (1x1).
        int[][] tinyFloor = new int[1][1];
        robot.penDown();

        // Action: Try to move out of the 1x1 grid.
        robot.move(1, tinyFloor);

        // Assert: Robot should not move, but since it didn't move (hasMoved remains false in the code),
        // it actually bypasses the floor marking entirely based on their logic.
        assertEquals(0, robot.getX(), "X should remain 0 on a 1x1 grid.");
        assertEquals(0, robot.getY(), "Y should remain 0 on a 1x1 grid.");
    }

    // ==========================================
    // 2. INPUT VALIDATION BOUNDARIES (App.java)
    // ==========================================

    @Test
    public void testInvalidGridInitializationBounds() {
        // Action & Assert: Size 0 should be rejected by the regex/validation logic.
        assertFalse(App.isValidCommand("I 0"), "Grid size 0 should be rejected.");
        
        // Action & Assert: Negative sizes should be rejected.
        assertFalse(App.isValidCommand("I -5"), "Negative grid sizes should be rejected.");
    }

    @Test
    public void testValidMinimumGridInitialization() {
        // Action & Assert: Size 1 is the absolute minimum valid positive integer for the grid.
        assertTrue(App.isValidCommand("I 1"), "Grid size 1x1 should be accepted.");
    }
    
    @Test
    public void testInvalidMovementBounds() {
        // Action & Assert: Negative steps should fail App validation before ever calling Robot.move().
        assertFalse(App.isValidCommand("M -5"), "Negative movement steps should be rejected by the parser.");
    }
    
    @Test
    public void testValidZeroMovement() {
        // Action & Assert: 0 is technically a non-negative integer, so the parser should accept it.
        assertTrue(App.isValidCommand("M 0"), "Movement of 0 steps should be accepted as valid syntax.");
    }
}