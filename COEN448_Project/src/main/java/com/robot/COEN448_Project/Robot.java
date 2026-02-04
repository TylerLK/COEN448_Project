package com.robot.COEN448_Project;

//Importing User-Defined Enumerations
import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

/**
 * 
 * Robot Class
 * @author Sunil
 * @x The x-axis position of the robot.
 * @y The y-axis position of the robot.
 * @penOrientation The orientation of the robot's pen.
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
     * Puts the robot's pen down.
     */
    public void penDown() {
        this.penOrientation = PenOrientation.DOWN;
    }

    /**
     * Lifts the robot's pen up.
     */
    public void penUp() {
        this.penOrientation = PenOrientation.UP;
    }

    /**
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
     * @return The direction the robot is facing.
     */
    public Orientation getDirection() {
        return direction;
    }

    /**
     * Moves the robot in the current direction by the specified number of steps.
     * TODO: needs to be checked for out of bounds conditions if we try to move outside the floor. Maybe throw an exception to reprompt user to enter a new command.
     * @param steps The number of steps to move the robot (must be a non-negative integer).
     * @throws IllegalArgumentException if steps is negative.
     */
    public void move(int steps, int[][] floor){
        if (steps < 0) {
            throw new IllegalArgumentException("Steps must be a non-negative integer.");
        }
        int currentX = x;
        int currentY = y;
        switch (direction) {
            case NORTH:
                y += steps;
                break;
            case EAST:
                x += steps;
                break;
            case SOUTH:
                y -= steps;
                break;
            case WEST:
                x -= steps;
                break;
        }

        // Fill in the horizontal path (handles movement in both positive and negative x directions)
        if (currentX != x) {
            int stepX = (x > currentX) ? 1 : -1;
            for (int i = currentX; i != x; i += stepX) {
                floor[i][currentY] = 1;
            }
        }
        // Fill in the vertical path (handles movement in both positive and negative y directions)
        if (currentY != y) {
            int stepY = (y > currentY) ? 1 : -1;
            for (int i = currentY; i != y; i += stepY) {
                floor[currentX][i] = 1;
            }
        }
    }

    @Override
    public String toString() {
        return "Position: " + x + ", " + y + " - Pen: " + penOrientation + " - Facing: " + direction;
    }	
}
