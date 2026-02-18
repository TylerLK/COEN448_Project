package com.robot.COEN448_Project;

//Importing User-Defined Enumerations
import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

/**
 * Robot Class
 * 
 * @author Sunil
 * @x The x-axis position of the robot.
 * @y The y-axis position of the robot.
 * @penOrientation The orientation of the robot's pen.
 * @direction The direction the robot is facing.
 */
public class Robot {
    private int x;
    private int y;
    private PenOrientation penOrientation;
    private Orientation direction;

    /**
     * Robot initial position [0,0] pen up and facing North.
     */
    public Robot() {
        this.x = 0;
        this.y = 0;
        this.penOrientation = PenOrientation.UP;
        this.direction = Orientation.NORTH;
    }
 
    // Program Functions
    /**
     * [U|u] ==> Pen up.
     * <br><br>
     * Lifts the robot's pen up.
     */
    public void penUp() {
        this.penOrientation = PenOrientation.UP;
    }

    /**
     * [D|d] ==> Pen down.
     * <br><br>
     * Puts the robot's pen down.
     */
    public void penDown() {
        this.penOrientation = PenOrientation.DOWN;
    }

    /**
     * [R|r] ==> Turn right.
     * <br><br>
     * Turns the robot right. Adjusts based on current direction.
     */
    public void turnRight() {
        switch (direction) {
            case NORTH:
                direction = Orientation.EAST;
                break;
            case EAST:
                direction = Orientation.SOUTH;
                break;
            case SOUTH:
                direction = Orientation.WEST;
                break;
            case WEST:
                direction = Orientation.NORTH;
                break;
        }
    }

    /**
     * [L|l] ==> Turn left.
     * <br><br>
     * Turns the robot left. Adjusts based on current direction.
     */
    public void turnLeft() {
        switch (direction) {
            case NORTH:
                direction = Orientation.WEST;
                break;
            case EAST:
                direction = Orientation.NORTH;
                break;
            case SOUTH:
                direction = Orientation.EAST;
                break;
            case WEST:
                direction = Orientation.SOUTH;
                break;
        }
    }

    /**
     * [M s|m s] ==> Move forward s spaces (s is a non-negative integer).
     * <br><br>
     * Moves the robot in the current direction by the specified number of steps.
     * 
     * @param steps The number of steps to move the robot (must be a non-negative
     *              integer).
     * @param floor The floor (2D array) to move the robot on.
     * @throws IllegalArgumentException       if steps is negative.
     */
    public void move(int steps, int[][] floor) {
        if (steps < 0) {
            throw new IllegalArgumentException("Steps must be a non-negative integer.");
        }
        int dx = 0;
        int dy = 0;

        // update the change in x and y based on the current direction
        switch (direction) {
            case NORTH:
                dy = 1;
                break;
            case EAST:
                dx = 1;
                break;
            case SOUTH:
                dy = -1;
                break;
            case WEST:
                dx = -1;
                break;
        }

        boolean hasMoved = false;
        // iteratively move the robot while also updating the floor
        for (int i = 0; i < steps; i++) {
            int nextX = x + dx;
            int nextY = y + dy;
            if (!isInsideFloor(nextX, nextY, floor)) {
                break;
            }

            if (penOrientation == PenOrientation.DOWN) {
                // update the initial tile they were on
                if (!hasMoved) {
                    floor[x][y] = 1;
                }

                // update the new tile they move to
                x = nextX;
                y = nextY;
                floor[x][y] = 1;
            } else {
                x = nextX;
                y = nextY;
            }
            hasMoved = true;
        }
    }

    /**
     * Checks if the given positions are inside the floor.
     * @param x The x-axis position to check.
     * @param y The y-axis position to check.
     * @param floor The floor (2D array) to check.
     * @return True if the new positions would be inside the floor, false otherwise.
     */
    private boolean isInsideFloor(int x, int y, int[][] floor) {
        return x >= 0 && x < floor.length && y >= 0 && y < floor[x].length;
    }

    /**
     * [C|c] ==> Print the robot's current position, the position of the pen, and which direction it is facing.
     * <br><br>
     * Overrides the toString() method to provide the robot's current state.
     * @return A string representation of the robot's current position, pen orientation, and direction.
     */
    @Override
    public String toString() {
        return "Position: " + x + ", " + y + " - Pen: " + penOrientation + " - Facing: " + direction;
    }
    
    // Utility Functions
    /**
     * @return The x-axis position of the robot.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The y-axis position of the robot.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The orientation of the robot's pen (UP or DOWN)
     */
    public PenOrientation getPenOrientation() {
        return penOrientation;
    }
    
    /**
     * @return The direction the robot is facing.
     */
    public Orientation getDirection() {
        return direction;
    }
}
