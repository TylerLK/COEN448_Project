package com.robot.COEN448_Project;

//Importing User-Defined Enumerations
import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

/**
 * 
 * Robot Class
 * 
 * @author Sunil
 * @x The x-axis position of the robot.
 * @y The y-axis position of the robot.
 * @penOrientation The orientation of the robot's pen.
 * @direction The direction the robot is facing.
 * 
 */
public class Robot {
    private int x;
    private int y;
    private PenOrientation penOrientation;
    private Orientation direction;

    /**
     * 
     * Robot initial position [0,0] pen up and facing North.
     * 
     */
    public Robot() {
        this.x = 0;
        this.y = 0;
        this.penOrientation = PenOrientation.UP;
        this.direction = Orientation.NORTH;
    }
 
    // Program Functions
    /**
     * 
     * [U|u] ==> Pen up.
     * <br><br>
     * Lifts the robot's pen up.
     * 
     */
    public void penUp() {
        this.penOrientation = PenOrientation.UP;
    }

    /**
     * 
     * [D|d] ==> Pen down.
     * <br><br>
     * Puts the robot's pen down.
     * 
     */
    public void penDown() {
        this.penOrientation = PenOrientation.DOWN;
    }

    /**
     * 
     * [R|r] ==> Turn right.
     * <br><br>
     * Turns the robot right. Adjusts based on current direction.
     * 
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
     * 
     * [L|l] ==> Turn left.
     * <br><br>
     * Turns the robot left. Adjusts based on current direction.
     * 
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
     * 
     * [M s|m s] ==> Move forward s spaces (s is a non-negative integer).
     * <br><br>
     * Moves the robot in the current direction by the specified number of steps.
     * 
     * @param steps The number of steps to move the robot (must be a non-negative
     *              integer).
     * @param floor The floor (2D array) to move the robot on.
     * @throws IllegalArgumentException       if steps is negative.
     * @throws ArrayIndexOutOfBoundsException if the robot moves outside the floor.
     * 
     */
    public void move(int steps, int[][] floor) {
        if (steps < 0) {
            throw new IllegalArgumentException("Steps must be a non-negative integer.");
        }

        int oldX = x;
        int oldY = y;
        int newX;
        int newY;
        switch (direction) {
            case NORTH:
                newY = y + steps;
                if (newY >= floor.length) {
                    throw new ArrayIndexOutOfBoundsException("The robot tried to move outside the floor.");
                }
                this.y = newY;
                break;
            case EAST:
                newX = x + steps;
                if (newX >= floor.length) {
                    throw new ArrayIndexOutOfBoundsException("The robot tried to move outside the floor.");
                }
                this.x = newX;
                break;
            case SOUTH:
                newY = y - steps;
                if (newY < 0) {
                    throw new ArrayIndexOutOfBoundsException("The robot tried to move outside the floor.");
                }
                this.y = newY;
                break;
            case WEST:
                newX = x - steps;
                if (newX < 0) {
                    throw new ArrayIndexOutOfBoundsException("The robot tried to move outside the floor.");
                }
                this.x = newX;
                break;
        }

        // After updating the position of the robot, we fill in the path it has taken if the pen is down
        if (penOrientation == PenOrientation.DOWN) {
            // Fill in the horizontal path (handles movement in both positive and negative x
            // directions)
            // if the x position has changed
            if (oldX != this.x) {
                if (oldX > this.x) {
                    for (int i = oldX; i >= this.x; i--) {
                        floor[i][oldY] = 1;
                    }
                } else {
                    for (int i = oldX; i <= this.x; i++) {
                        floor[i][oldY] = 1;
                    }
                }
            }
            // Fill in the vertical path (handles movement in both positive and negative y
            // directions)
            // if the y position has changed
            if (oldY != this.y) {
                if (oldY > this.y) {
                    for (int i = oldY; i >= this.y; i--) {
                        floor[oldX][i] = 1;
                    }
                } else {
                    for (int i = oldY; i <= this.y; i++) {
                        floor[oldX][i] = 1;
                    }
                }
            }
        }
    }

    /**
     * 
     * [C|c] ==> Print the robot's current position, the position of the pen, and which direction it is facing.
     * <br><br>
     * Overrides the toString() method to provide the robot's current state.
     * @return A string representation of the robot's current position, pen orientation, and direction.
     * 
     */
    @Override
    public String toString() {
        return "Position: " + x + ", " + y + " - Pen: " + penOrientation + " - Facing: " + direction;
    }
    
    //Utility Functions
    /**
     * 
     * @return The x-axis position of the robot.
     * 
     */
    public int getX() {
        return x;
    }

    /**
     * 
     * @return The y-axis position of the robot.
     * 
     */
    public int getY() {
        return y;
    }

    /**
     * 
     * @return The orientation of the robot's pen (UP or DOWN)
     * 
     */
    public PenOrientation getPenOrientation() {
        return penOrientation;
    }
    
    /**
     * 
     * @return The direction the robot is facing.
     * 
     */
    public Orientation getDirection() {
        return direction;
    }
}
