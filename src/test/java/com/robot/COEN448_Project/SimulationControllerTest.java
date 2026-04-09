package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

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

    // ─── TC-ML-01 to TC-ML-08: SimulationController loop tests ──────────────

    /** TC-ML-01: Valid floor size on first try – no error, exits cleanly. */
    @Test
    public void tcMl01ValidFloorSizeOnFirstTry() {
        SimulationController controller = createController("5\nq\n");
        String output = captureStdout(controller::run);

        assertFalse(output.contains("N must be an integer greater than 0"));
        assertFalse(output.contains("Please enter a whole number"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-02: Zero then valid – integer-range error printed exactly once. */
    @Test
    public void tcMl02ZeroThenValidFloorSize() {
        SimulationController controller = createController("0\n5\nq\n");
        String output = captureStdout(controller::run);

        assertEquals(1, countOccurrences(output, "N must be an integer greater than 0"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-03: Negative integer then valid – same integer-range error once. */
    @Test
    public void tcMl03NegativeIntegerThenValidFloorSize() {
        SimulationController controller = createController("-1\n5\nq\n");
        String output = captureStdout(controller::run);

        assertEquals(1, countOccurrences(output, "N must be an integer greater than 0"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-04: Non-integer "abc" then valid – whole-number format error once. */
    @Test
    public void tcMl04NonIntegerThenValidFloorSize() {
        SimulationController controller = createController("abc\n5\nq\n");
        String output = captureStdout(controller::run);

        assertEquals(1, countOccurrences(output, "Please enter a whole number"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-05: Four bad inputs ("0","abc","-1","3.5") then valid "5" – at least 4 errors. */
    @Test
    public void tcMl05MultipleInvalidsThenValidFloorSize() {
        SimulationController controller = createController("0\nabc\n-1\n3.5\n5\nq\n");
        String output = captureStdout(controller::run);

        long intErrors    = countOccurrences(output, "N must be an integer greater than 0");
        long formatErrors = countOccurrences(output, "Please enter a whole number");

        assertTrue(intErrors + formatErrors >= 4,
            "Expected >= 4 error messages, got " + (intErrors + formatErrors));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-06: After "5\nq\n", commandHistory first entry is "I 5". */
    @Test
    public void tcMl06HistorySeededWithInitCommand() {
        SimulationController controller = createController("5\nq\n");
        captureStdout(controller::run);

        CommandExecutor exec = (CommandExecutor) getField(controller, "executor");
        @SuppressWarnings("unchecked")
        Queue<String> history = (Queue<String>) getField(exec, "commandHistory");

        assertFalse(history.isEmpty(), "History should not be empty after initialization");
        assertEquals("I 5", history.peek(), "First history entry should be \"I 5\"");
    }

    /** TC-ML-07: Three commands (u, d, q) → "Available Commands:" appears exactly 3 times. */
    @Test
    public void tcMl07MenuPrintedOncePerIteration() {
        SimulationController controller = createController("5\nu\nd\nq\n");
        String output = captureStdout(controller::run);

        assertEquals(3, countOccurrences(output, "Available Commands:"),
            "Menu header should appear once per command iteration");
    }

    /** TC-ML-08: d, m3, r, m2, q → robot ends at (2,3) EAST pen DOWN. */
    @Test
    public void tcMl08StatePersistsAcrossCommands() {
        SimulationController controller = createController("5\nd\nm 3\nr\nm 2\nq\n");
        captureStdout(controller::run);

        CommandExecutor exec  = (CommandExecutor) getField(controller, "executor");
        Robot           robot = (Robot)           getField(exec,        "robot");

        assertEquals(2,                   robot.getX(),              "Robot x should be 2");
        assertEquals(3,                   robot.getY(),              "Robot y should be 3");
        assertEquals(Orientation.EAST,    robot.getDirection(),      "Robot should face EAST");
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation(), "Pen should be DOWN");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static SimulationController createController(String input) {
        return new SimulationController(
            new CommandParser(),
            new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)))
        );
    }

    private static long countOccurrences(String text, String pattern) {
        long count = 0;
        int idx = 0;
        while ((idx = text.indexOf(pattern, idx)) != -1) {
            count++;
            idx += pattern.length();
        }
        return count;
    }

    private static Object getField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field: " + fieldName, e);
        }
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
