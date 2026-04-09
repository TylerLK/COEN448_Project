package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

public class CommandExecutorTest {
    private CommandExecutor executor;

    @BeforeEach
    public void setUp() {
        executor = new CommandExecutor(new CommandParser());
        executor.initialize(4);
    }

    @Test
    public void initializeCreatesCleanRobotAndFloor() {
        executor.initialize(3);

        int[][] floor = getFloor();
        Robot robot = getRobot();

        assertEquals(3, floor.length);
        assertEquals(3, floor[0].length);
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void executeCommandMovesAndMarksWithPenDown() {
        executor.executeCommand("D", true);
        executor.executeCommand("m 2", true);

        Robot robot = getRobot();
        int[][] floor = getFloor();

        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation());
        assertEquals(1, floor[0][0]);
        assertEquals(1, floor[0][1]);
        assertEquals(1, floor[0][2]);
    }

    @Test
    public void executeCommandTrimsWhitespaceAndAcceptsUppercase() {
        executor.executeCommand("  d  ", true);
        executor.executeCommand("  M 1  ", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());
        assertEquals(2, countMarks(getFloor()));
    }

    @Test
    public void executeCommandRejectsInvalidInputWithoutChangingState() {
        String output = captureStdout(() -> executor.executeCommand("m -1", true));

        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
        assertEquals(0, getRobot().getX());
        assertEquals(0, getRobot().getY());
        assertEquals(0, getHistory().size());
    }

    @Test
    public void executeCommandPrintsRobotState() {
        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);

        String output = captureStdout(() -> executor.executeCommand("c", true));
        assertTrue(output.contains("Position: 0, 1 - Pen: DOWN - Facing: NORTH"));
    }

    @Test
    public void executeCommandPrintRendersFloor() {
        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);

        String output = captureStdout(() -> executor.executeCommand("p", true));
        assertTrue(output.contains("*"));
        assertTrue(output.contains("0"));
        assertTrue(output.contains("1"));
    }

    @Test
    public void executeCommandQuitStopsProgram() {
        String output = captureStdout(() -> executor.executeCommand("q", true));

        assertFalse(executor.isRunning());
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    @Test
    public void executeCommandDoesNotAddToHistoryWhenFlagIsFalse() {
        executor.executeCommand("u", false);

        assertEquals(0, getHistory().size());
    }

    @Test
    public void historyReplaysCommandsAndRestoresQueue() {
        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);
        executor.executeCommand("r", true);
        executor.executeCommand("m 1", true);

        Queue<String> before = new ArrayDeque<>(getHistory());
        String output = captureStdout(() -> executor.executeCommand("h", true));

        assertTrue(output.contains("End of Command History."));
        assertEquals(before.size(), getHistory().size());
        assertEquals(before.poll(), getHistory().poll());
        assertEquals(before.poll(), getHistory().poll());
        assertEquals(before.poll(), getHistory().poll());
        assertEquals(before.poll(), getHistory().poll());

        Robot robot = getRobot();
        assertEquals(2, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(Orientation.SOUTH, robot.getDirection());
    }

    @Test
    public void executeCommandInitializeResetsRobotAndFloor() {
        executor.executeCommand("d", true);
        executor.executeCommand("m 3", true);
        executor.executeCommand("i 2", true);

        Robot robot = getRobot();
        int[][] floor = getFloor();

        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
        assertEquals(2, floor.length);
        assertEquals(2, floor[0].length);
    }

    private Robot getRobot() {
        return (Robot) getFieldValue("robot");
    }

    private int[][] getFloor() {
        return (int[][]) getFieldValue("floor");
    }

    @SuppressWarnings("unchecked")
    private Queue<String> getHistory() {
        return (Queue<String>) getFieldValue("commandHistory");
    }

    private Object getFieldValue(String fieldName) {
        try {
            Field field = CommandExecutor.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(executor);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
