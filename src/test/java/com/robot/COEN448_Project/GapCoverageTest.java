package com.robot.COEN448_Project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Queue;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

public class GapCoverageTest {

    private CommandParser parser;
    private CommandExecutor executor;

    @BeforeEach
    public void setUp() {
        parser = new CommandParser();
        executor = new CommandExecutor(parser);
        // 10x10 gives enough room for 5-step moves used in TC-IV-01b through TC-IV-03b.
        executor.initialize(10);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static SimulationController createController(String input) {
        CommandParser p = new CommandParser();
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new SimulationController(p, scanner);
    }

    private static CommandExecutor getExecutor(SimulationController controller) {
        return (CommandExecutor) getField(controller, "executor");
    }

    private static Robot getRobotFrom(CommandExecutor exec) {
        return (Robot) getField(exec, "robot");
    }

    @SuppressWarnings("unchecked")
    private static Queue<String> getHistoryFrom(CommandExecutor exec) {
        return (Queue<String>) getField(exec, "commandHistory");
    }

    private static int[][] getFloorFrom(CommandExecutor exec) {
        return (int[][]) getField(exec, "floor");
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

    private Robot getRobot() {
        return getRobotFrom(executor);
    }

    private int[][] getFloor() {
        return getFloorFrom(executor);
    }

    private static int countFloorMarks(int[][] floor) {
        int count = 0;
        for (int[] col : floor) {
            for (int cell : col) {
                if (cell == 1) count++;
            }
        }
        return count;
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

    private static String captureStdout(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
            return buffer.toString();
        } finally {
            System.setOut(originalOut);
        }
    }

    // ─── Gap Group 1: SimulationController loop tests (TC-ML-01 to TC-ML-08) ─

    /** TC-ML-01: Valid floor size on first try – no error, exits cleanly. */
    @Test
    public void tcMl01ValidFloorSizeOnFirstTry() {
        SimulationController controller = createController("5\nq\n");
        String output = captureStdout(controller::run);

        assertFalse(output.contains("N must be an integer greater than 0"));
        assertFalse(output.contains("Please enter a whole number"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-02: Zero then valid – integer-range error printed exactly once, program runs. */
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
            "Expected ≥ 4 error messages, got " + (intErrors + formatErrors));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    /** TC-ML-06: After "5\nq\n", commandHistory first entry is "I 5". */
    @Test
    public void tcMl06HistorySeededWithInitCommand() {
        SimulationController controller = createController("5\nq\n");
        captureStdout(controller::run);

        CommandExecutor exec    = getExecutor(controller);
        Queue<String>   history = getHistoryFrom(exec);

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

        CommandExecutor exec  = getExecutor(controller);
        Robot           robot = getRobotFrom(exec);

        assertEquals(2,                  robot.getX(),              "Robot x should be 2");
        assertEquals(3,                  robot.getY(),              "Robot y should be 3");
        assertEquals(Orientation.EAST,   robot.getDirection(),      "Robot should face EAST");
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation(), "Pen should be DOWN");
    }

    // ─── Gap Group 2: CommandParser/CommandExecutor validation (TC-IV-01b to TC-IV-15) ─

    /** TC-IV-01b: "m  5" (double space) – accepted, robot moves to (0,5). */
    @Test
    public void tcIv01bDoubleSpaceAccepted() {
        executor.executeCommand("m  5", true);
        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(5, robot.getY());
    }

    /** TC-IV-02b: "m\t5" (tab separator) – accepted, robot moves to (0,5). */
    @Test
    public void tcIv02bTabSeparatorAccepted() {
        executor.executeCommand("m\t5", true);
        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(5, robot.getY());
    }

    /** TC-IV-03b: "  m   5  " (leading/trailing whitespace) – accepted, robot moves to (0,5). */
    @Test
    public void tcIv03bLeadingTrailingWhitespaceAccepted() {
        executor.executeCommand("  m   5  ", true);
        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(5, robot.getY());
    }

    /** TC-IV-04: "m 2147483648" (Integer.MAX_VALUE + 1 overflows) – rejected, robot unmoved. */
    @Test
    public void tcIv04IntegerOverflowInMoveRejected() {
        String output = captureStdout(() -> executor.executeCommand("m 2147483648", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    /** TC-IV-05: "i 2147483648" – rejected with size error, floor size unchanged. */
    @Test
    public void tcIv05IntegerOverflowInInitRejected() {
        int sizeBefore = getFloor().length;
        String output = captureStdout(() -> executor.executeCommand("i 2147483648", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));
        assertEquals(sizeBefore, getFloor().length);
    }

    /** TC-IV-06: "m 3.5" – float format rejected with distance error. */
    @Test
    public void tcIv06FloatMoveRejected() {
        String output = captureStdout(() -> executor.executeCommand("m 3.5", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
    }

    /** TC-IV-07: "i 4.0" – float format rejected with size error, floor unchanged. */
    @Test
    public void tcIv07FloatInitRejected() {
        int sizeBefore = getFloor().length;
        String output = captureStdout(() -> executor.executeCommand("i 4.0", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));
        assertEquals(sizeBefore, getFloor().length);
    }

    /** TC-IV-08: "m 1e5" – scientific notation rejected with distance error. */
    @Test
    public void tcIv08ScientificNotationMoveRejected() {
        String output = captureStdout(() -> executor.executeCommand("m 1e5", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
    }

    /**
     * TC-IV-09: "m -0" – Integer.parseInt("-0") == 0, so command is accepted as a 0-step move.
     * Pen is set DOWN first; assert no marks and robot stays at (0,0).
     */
    @Test
    public void tcIv09NegativeZeroTreatedAsZeroStep() {
        executor.executeCommand("d", true);  // pen DOWN so any actual movement would leave marks
        executor.executeCommand("m -0", true);

        Robot   robot = getRobot();
        int[][] floor = getFloor();

        assertEquals(0, robot.getX(), "Robot x should remain 0");
        assertEquals(0, robot.getY(), "Robot y should remain 0");
        assertEquals(0, countFloorMarks(floor), "No floor cells should be marked after 0-step move");
    }

    /** TC-IV-10: "m +5" – Integer.parseInt("+5") == 5, accepted; robot moves to (0,5). */
    @Test
    public void tcIv10PlusPrefixAccepted() {
        executor.executeCommand("m +5", true);
        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(5, robot.getY());
    }

    /** TC-IV-11: "x 5" (unknown two-token command) – rejected with generic invalid-command error. */
    @Test
    public void tcIv11UnknownTwoTokenCommandRejected() {
        String output = captureStdout(() -> executor.executeCommand("x 5", true));
        assertTrue(output.contains("Invalid Command. Please try again."));
    }

    /** TC-IV-12: "@", "!", "#5" – each rejected with generic invalid-command error. */
    @Test
    public void tcIv12SpecialCharacterCommandsRejected() {
        String out1 = captureStdout(() -> executor.executeCommand("@", true));
        assertTrue(out1.contains("Invalid Command. Please try again."), "@ should be rejected");

        String out2 = captureStdout(() -> executor.executeCommand("!", true));
        assertTrue(out2.contains("Invalid Command. Please try again."), "! should be rejected");

        String out3 = captureStdout(() -> executor.executeCommand("#5", true));
        assertTrue(out3.contains("Invalid Command. Please try again."), "#5 should be rejected");
    }

    /** TC-IV-13: "m 0x10" – hexadecimal notation rejected with distance error. */
    @Test
    public void tcIv13HexNotationMoveRejected() {
        String output = captureStdout(() -> executor.executeCommand("m 0x10", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
    }

    /** TC-IV-14: "\t" and "\n" – whitespace-only inputs rejected with empty-command error. */
    @Test
    public void tcIv14WhitespaceOnlyInputsRejected() {
        String out1 = captureStdout(() -> executor.executeCommand("\t", true));
        assertTrue(out1.contains("Empty Command. Please try again."), "Tab-only input should be rejected");

        String out2 = captureStdout(() -> executor.executeCommand("\n", true));
        assertTrue(out2.contains("Empty Command. Please try again."), "Newline-only input should be rejected");
    }

    /**
     * TC-IV-15: Six consecutive invalid commands – robot state fully unchanged,
     * pen UP, facing NORTH, zero floor marks.
     */
    @Test
    public void tcIv15InvalidCommandSequencePreservesRobotState() {
        captureStdout(() -> {
            for (String cmd : new String[]{"m 3.5", "i 4.0", "m 2147483648", "@", "\t", "x 5"}) {
                executor.executeCommand(cmd, true);
            }
        });

        Robot   robot = getRobot();
        int[][] floor = getFloor();

        assertEquals(0,                    robot.getX(),              "Robot x should be 0");
        assertEquals(0,                    robot.getY(),              "Robot y should be 0");
        assertEquals(PenOrientation.UP,    robot.getPenOrientation(), "Pen should be UP");
        assertEquals(Orientation.NORTH,    robot.getDirection(),      "Robot should face NORTH");
        assertEquals(0,                    countFloorMarks(floor),    "No floor cells should be marked");
    }
}
