package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class SimulationControllerTest {

    @Test
    public void printMenuListsAllCommands() {
        SimulationController controller = new SimulationController(new CommandParser(), new Scanner(new ByteArrayInputStream(new byte[0])));
        String output = captureStdout(controller::printMenu);

        assertTrue(output.contains("Available Commands:"));
        assertTrue(output.contains("[U|u]      |  Pen Up"));
        assertTrue(output.contains("[D|d]      |  Pen Down"));
        assertTrue(output.contains("[R|r]      |  Turn Right"));
        assertTrue(output.contains("[L|l]      |  Turn Left"));
        assertTrue(output.contains("[M s|m s]  |  Move Forward s Spaces"));
        assertTrue(output.contains("[P|p]      |  Print the Floor"));
        assertTrue(output.contains("[C|c]      |  Print the Robot's Current Position and Direction"));
        assertTrue(output.contains("[I n|i n]  |  Initialize the System"));
        assertTrue(output.contains("[H|h]      |  Replay Command History"));
        assertTrue(output.contains("[Q|q]      |  Stop the Program"));
    }

    @Test
    public void runInitializesAndStopsOnQuit() {
        SimulationController controller = new SimulationController(
            new CommandParser(),
            new Scanner(new ByteArrayInputStream("1\nq\n".getBytes(StandardCharsets.UTF_8)))
        );

        String output = captureStdout(controller::run);

        assertTrue(output.contains("Please enter the size you would like the floor to be (N x N):"));
        assertTrue(output.contains("Available Commands:"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    @Test
    public void runRetriesAfterInvalidFloorInputs() {
        SimulationController controller = new SimulationController(
            new CommandParser(),
            new Scanner(new ByteArrayInputStream("0\nabc\n2\nq\n".getBytes(StandardCharsets.UTF_8)))
        );

        String output = captureStdout(controller::run);

        assertTrue(output.contains("Invalid input. N must be an integer greater than 0."));
        assertTrue(output.contains("Invalid input. Please enter a whole number (e.g., 5)."));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    private static String captureStdout(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        try {
            action.run();
            return out.toString();
        } finally {
            System.setOut(originalOut);
        }
    }
}
