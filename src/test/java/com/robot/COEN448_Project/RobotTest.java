package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

public class RobotTest {

    @Test
    public void initialStateIsOriginPenUpFacingNorth() {
        Robot robot = new Robot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void penTransitionsWork() {
        Robot robot = new Robot();
        robot.penDown();
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation());
        robot.penUp();
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
    }

    @Test
    public void turningRightCyclesThroughDirections() {
        Robot robot = new Robot();
        robot.turnRight();
        assertEquals(Orientation.EAST, robot.getDirection());
        robot.turnRight();
        assertEquals(Orientation.SOUTH, robot.getDirection());
        robot.turnRight();
        assertEquals(Orientation.WEST, robot.getDirection());
        robot.turnRight();
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void turningLeftCyclesThroughDirections() {
        Robot robot = new Robot();
        robot.turnLeft();
        assertEquals(Orientation.WEST, robot.getDirection());
        robot.turnLeft();
        assertEquals(Orientation.SOUTH, robot.getDirection());
        robot.turnLeft();
        assertEquals(Orientation.EAST, robot.getDirection());
        robot.turnLeft();
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void movementFollowsDirection() {
        int[][] floor = new int[5][5];
        Robot robot = new Robot();

        robot.move(1, floor); // NORTH
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());

        robot.turnRight(); // EAST
        robot.move(2, floor);
        assertEquals(2, robot.getX());
        assertEquals(1, robot.getY());

        robot.turnRight(); // SOUTH
        robot.move(1, floor);
        assertEquals(2, robot.getX());
        assertEquals(0, robot.getY());

        robot.turnRight(); // WEST
        robot.move(2, floor);
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    public void moveWithPenUpDoesNotMarkFloor() {
        int[][] floor = new int[4][4];
        Robot robot = new Robot();

        robot.move(3, floor);

        assertEquals(0, countMarks(floor));
    }

    @Test
    public void moveWithPenDownMarksPath() {
        int[][] floor = new int[4][4];
        Robot robot = new Robot();

        robot.penDown();
        robot.move(2, floor);

        assertEquals(3, countMarks(floor));
        assertEquals(1, floor[0][0]);
        assertEquals(1, floor[0][1]);
        assertEquals(1, floor[0][2]);
    }

    @Test
    public void moveZeroStepsDoesNotChangePositionOrMark() {
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        robot.penDown();
        robot.move(0, floor);

        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(0, countMarks(floor));
    }

    @Test
    public void moveBeyondGridStopsAtBoundary() {
        int[][] floor = new int[2][2];
        Robot robot = new Robot();

        robot.move(3, floor);
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());
    }

    @Test
    public void moveStopsAtNorthBoundary() {
        // Tests isInsideFloor for y >= floor[x].length
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        // Move to (0, 2) which is the north edge
        robot.move(2, floor);
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());

        // Try to move further north - should stop at boundary
        robot.move(5, floor);
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY()); // Still at y=2, didn't move to y=3 or beyond
    }

    @Test
    public void moveStopsAtEastBoundary() {
        // Tests isInsideFloor for x >= floor.length
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        robot.turnRight(); // Face EAST
        robot.move(2, floor); // Move to (2, 0) which is the east edge
        assertEquals(2, robot.getX());
        assertEquals(0, robot.getY());

        // Try to move further east - should stop at boundary
        robot.move(5, floor);
        assertEquals(2, robot.getX()); // Still at x=2, didn't move to x=3 or beyond
        assertEquals(0, robot.getY());
    }

    @Test
    public void moveStopsAtSouthBoundary() {
        // Tests isInsideFloor for y < 0
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        // Robot starts at (0, 0) which is the south edge
        robot.turnRight(); // Face EAST
        robot.turnRight(); // Face SOUTH

        // Try to move south - should not move below y=0
        robot.move(5, floor);
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY()); // Still at y=0, didn't move to y=-1 or below
    }

    @Test
    public void moveStopsAtWestBoundary() {
        // Tests isInsideFloor for x < 0
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        // Robot starts at (0, 0) which is the west edge
        robot.turnLeft(); // Face WEST

        // Try to move west - should not move below x=0
        robot.move(5, floor);
        assertEquals(0, robot.getX()); // Still at x=0, didn't move to x=-1 or below
        assertEquals(0, robot.getY());
    }

    @Test
    public void moveWithPenDownStopsAtBoundaryAndMarksPath() {
        // Tests that boundary checking works correctly with pen down
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        robot.penDown();
        robot.move(10, floor); // Try to move 10 spaces north on 3x3 grid

        // Should move from (0,0) to (0,2) and mark the path
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());
        assertEquals(3, countMarks(floor)); // Marks at (0,0), (0,1), (0,2)
        assertEquals(1, floor[0][0]);
        assertEquals(1, floor[0][1]);
        assertEquals(1, floor[0][2]);
    }

    @Test
    public void moveInAllDirectionsRespectsBoundaries() {
        // Comprehensive test for isInsideFloor in all directions
        int[][] floor = new int[2][2];
        Robot robot = new Robot();

        // Test all four boundaries in sequence
        robot.move(10, floor); // North: should stop at y=1
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());

        robot.turnRight(); // EAST
        robot.move(10, floor); // East: should stop at x=1
        assertEquals(1, robot.getX());
        assertEquals(1, robot.getY());

        robot.turnRight(); // SOUTH
        robot.move(10, floor); // South: should stop at y=0
        assertEquals(1, robot.getX());
        assertEquals(0, robot.getY());

        robot.turnRight(); // WEST
        robot.move(10, floor); // West: should stop at x=0
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    private static int countMarks(int[][] floor) {
        int count = 0;
        for (int x = 0; x < floor.length; x++) {
            for (int y = 0; y < floor[x].length; y++) {
                if (floor[x][y] == 1) {
                    count++;
                }
            }
        }
        return count;
    }
}
