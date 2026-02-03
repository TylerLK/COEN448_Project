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
     * @set The x-axis position of the robot.
     */
    public void setX(int x) {
    	this.x = x;
    }
    
    /**
     * @set The y-axis position of the robot.
     */
    public void setY(int y) {
    	this.y = y;
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
     * @set The direction the robot is facing.
     */
    public void setDirection(Orientation direction) {
        this.direction = direction;
    }

    /**
     * Moves the robot in the current direction by the specified number of steps.
     * TODO: needs to be checked for out of bounds conditions if we try to move outside the floor. Maybe throw an exception to reprompt user to enter a new command.
     * @param steps The number of steps to move the robot (must be a non-negative integer).
     * @throws IllegalArgumentException if steps is negative.
     */
    public void move(int steps){
        if (steps < 0) {
            throw new IllegalArgumentException("Steps must be a non-negative integer.");
        }
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
    }

    @Override
    public String toString() {
        return "Position: " + x + ", " + y + " - Pen: " + penOrientation + " - Facing: " + direction;
    }	
}
