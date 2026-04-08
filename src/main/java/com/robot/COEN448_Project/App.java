package com.robot.COEN448_Project;

/**
 * App Class
 * 
 * Entry point for the Robot Simulation program.
 * Separates concerns by delegating simulation logic to SimulationController.
 */
public class App {
    public static void main(String[] args) {
        SimulationController controller = new SimulationController();
        controller.run();
    }
}
