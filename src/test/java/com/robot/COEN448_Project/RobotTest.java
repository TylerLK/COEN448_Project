package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void turningCyclesThroughAllDirections() {
        Robot robot = new Robot();

        robot.turnRight();
        assertEquals(Orientation.EAST, robot.getDirection());
        robot.turnRight();
        assertEquals(Orientation.SOUTH, robot.getDirection());
        robot.turnRight();
        assertEquals(Orientation.WEST, robot.getDirection());
        robot.turnRight();
        assertEquals(Orientation.NORTH, robot.getDirection());

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
    public void moveWithPenUpDoesNotMarkFloor() {
        int[][] floor = new int[4][4];
        Robot robot = new Robot();

        robot.move(3, floor);

        assertEquals(0, robot.getX());
        assertEquals(3, robot.getY());
        assertEquals(0, countMarks(floor));
    }

    @Test
    public void moveWithPenDownMarksPath() {
        int[][] floor = new int[4][4];
        Robot robot = new Robot();

        robot.penDown();
        robot.move(2, floor);

        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());
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
    public void moveNegativeStepsThrowsIllegalArgumentException() {
        int[][] floor = new int[3][3];
        Robot robot = new Robot();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> robot.move(-1, floor)
        );

        assertEquals("Steps must be a non-negative integer.", exception.getMessage());
    }

    @Test
    public void moveStopsAtGridBoundaries() {
        int[][] floor = new int[2][2];
        Robot robot = new Robot();

        robot.move(10, floor);
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());

        robot.turnRight();
        robot.move(10, floor);
        assertEquals(1, robot.getX());
        assertEquals(1, robot.getY());

        robot.turnRight();
        robot.move(10, floor);
        assertEquals(1, robot.getX());
        assertEquals(0, robot.getY());

        robot.turnRight();
        robot.move(10, floor);
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
