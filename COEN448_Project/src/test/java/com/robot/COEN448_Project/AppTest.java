package com.robot.COEN448_Project;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;

public class AppTest {

    @Test
    public void initializeCreatesCleanFloorAndRobot() {
        resetAppState(4);

        int[][] floor = getFloor();
        assertEquals(4, floor.length);
        for (int i = 0; i < floor.length; i++) {
            assertEquals(4, floor[i].length);
        }
        assertAllZeros(floor);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void executeCommandMovesAndMarksWithPenDown() {
        resetAppState(5);

        App.executeCommand("D", true);
        App.executeCommand("m 2", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());

        int[][] floor = getFloor();
        assertEquals(3, countMarks(floor));
        assertEquals(1, floor[0][0]);
        assertEquals(1, floor[0][1]);
        assertEquals(1, floor[0][2]);
    }

    @Test
    void moveWithNegativeStepsThrowsIllegalArgumentException() {
        int[][] floor = new int[5][5];
        Robot robot = new Robot();

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> robot.move(-1, floor),
            "Negative steps should throw IllegalArgumentException"
        );

        assertEquals(
            "Steps must be a non-negative integer.",
            ex.getMessage()
        );
    }

    @Test
    void moveOutsideGridThrowsArrayIndexOutOfBoundsException() {
        int[][] floor = new int[2][2];
        Robot robot = new Robot();

        robot.penDown();

        ArrayIndexOutOfBoundsException ex = assertThrows(
            ArrayIndexOutOfBoundsException.class,
            () -> robot.move(5, floor)
        );

        assertEquals(
            "The robot tried to move outside the floor.",
            ex.getMessage()
        );
    }

    @Test
    public void executeCommandPenUpAndDownAffectsRobot() {
        resetAppState(3);

        App.executeCommand("d", true);
        assertEquals(PenOrientation.DOWN, getRobot().getPenOrientation());

        App.executeCommand("u", true);
        assertEquals(PenOrientation.UP, getRobot().getPenOrientation());
    }

    @Test
    public void executeCommandTurnsUpdateDirection() {
        resetAppState(3);

        App.executeCommand("r", true);
        assertEquals(Orientation.EAST, getRobot().getDirection());

        App.executeCommand("l", true);
        assertEquals(Orientation.NORTH, getRobot().getDirection());
    }

    @Test
    public void executeCommandMoveZeroDoesNotMarkOrMove() {
        resetAppState(3);

        App.executeCommand("d", true);
        App.executeCommand("m 0", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(0, countMarks(getFloor()));
    }

    @Test
    public void executeCommandNegativeMovePrintsInvalidCommand() {
        resetAppState(3);

        String output = captureStdout(() -> App.executeCommand("m -1", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
    }

    @Test
    public void executeCommandTrimsInputAndAcceptsUppercase() {
        resetAppState(3);

        App.executeCommand("  d  ", true);
        App.executeCommand("  M 1  ", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());
        assertEquals(2, countMarks(getFloor()));
    }

    @Test
    public void executeCommandInvalidCommandPrintsMessage() {
        resetAppState(3);

        String output = captureStdout(() -> App.executeCommand("x", true));
        assertTrue(output.contains("Invalid Command. Please try again."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void executeCommandMissingMoveArgumentThrows() {
        resetAppState(3);
        String output = captureStdout(() -> App.executeCommand("m", true));
        assertTrue(output.contains("Invalid Command. Incorrect number of arguments for this command."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());  
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());

    }

    @Test
    public void executeCommandMissingInitArgumentThrows() {
        resetAppState(3);
        String output = captureStdout(() -> App.executeCommand("i", true));
        assertTrue(output.contains("Invalid Command. Incorrect number of arguments for this command."));

         Robot robot = getRobot();
        assertEquals(0, robot.getX());  
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());

        
    }

    @Test
    public void executeCommandNonNumericMoveArgumentThrows() {
        resetAppState(3);
        String output = captureStdout(() -> App.executeCommand("m x", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());

    }

    @Test
    public void executeCommandEmptyOrBlankCommandPrintsMessage() {
        resetAppState(3);

        String emptyOutput = captureStdout(() -> App.executeCommand("", true));
        assertTrue(emptyOutput.contains("Empty Command. Please try again."));

        String blankOutput = captureStdout(() -> App.executeCommand("   ", true));
        assertTrue(blankOutput.contains("Empty Command. Please try again."));
    }

    @Test
    public void executeCommandNullCommandThrows() {
        resetAppState(3);
        String output = captureStdout(() -> App.executeCommand(null, true));
        assertTrue(output.contains("Empty Command. Please try again."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());  
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

   

    @Test
    public void executeCommandInitWithExtraArgsUsesFirstArg() {
        resetAppState(3);

        App.executeCommand("i 4 extra", true);

        int[][] floor = getFloor();
        assertEquals(3, floor.length);
        for (int i = 0; i < floor.length; i++) {
            assertEquals(3, floor[i].length);
        }
        assertAllZeros(floor);
    }

    @Test
    public void executeCommandNonNumericInitArgumentThrows() {
        resetAppState(3);
 String output = captureStdout(() -> App.executeCommand("i x", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));
    }

    @Test
    public void executeCommandNegativeInitArgumentThrows() {
        resetAppState(3);
        String output = captureStdout(() -> App.executeCommand("i -1", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));

    }

    @Test
    public void executeCommandZeroInitCreatesEmptyFloorAndResetsRobot() {
        resetAppState(3);
        App.executeCommand("d", true);
        App.executeCommand("m 1", true);

        App.executeCommand("i 1", true);

        String output = captureStdout(() -> App.executeCommand("i 0", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));

        int[][] floor = getFloor();
        assertEquals(1, floor.length);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void executeCommandPrintsRobotState() {
        resetAppState(3);
        App.executeCommand("d", true);
        App.executeCommand("m 1", true);

        String output = captureStdout(() -> App.executeCommand("c", true));
        assertTrue(output.contains("Position: 0, 1 - Pen: DOWN - Facing: NORTH"));
    }

    @Test
    public void executeCommandPrintRendersStars() {
        resetAppState(2);
        App.executeCommand("d", true);
        App.executeCommand("m 1", true);

        String output = captureStdout(() -> App.executeCommand("p", true));
        long starCount = output.chars().filter(ch -> ch == '*').count();
        assertEquals(2L, starCount);
        assertTrue(output.contains("0"));
        assertTrue(output.contains("1 "));
    }

    @Test
    public void executeCommandQuitStopsProgram() {
        resetAppState(2);

        String output = captureStdout(() -> App.executeCommand("q", true));
        assertFalse(getIsRunning());
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    @Test
    public void initializeCommandResetsGridAndRobot() {
        resetAppState(4);
        App.executeCommand("d", true);
        App.executeCommand("m 3", true);

        App.executeCommand("i 2", true);

        int[][] floor = getFloor();
        assertEquals(2, floor.length);
        for (int i = 0; i < floor.length; i++) {
            assertEquals(2, floor[i].length);
        }
        assertAllZeros(floor);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void executeCommandDoesNotAddToHistoryWhenFlagFalse() {
        resetAppState(3);

        App.executeCommand("d", false);
        Queue<String> history = getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void historyReplaysCommandsAndPreservesQueue() {
        resetAppState(4);

        App.executeCommand("d", true);
        App.executeCommand("m 1", true);
        App.executeCommand("r", true);
        App.executeCommand("m 1", true);

        Queue<String> before = new ArrayDeque<>(getHistory());

        String output = captureStdout(() -> App.executeCommand("h", true));
        assertTrue(output.contains("End of Command History."));

        Queue<String> after = getHistory();
        assertEquals(before.size(), after.size());
        while (!before.isEmpty()) {
            assertEquals(before.poll(), after.poll());
        }

        Robot robot = getRobot();
        assertEquals(2, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(Orientation.SOUTH, robot.getDirection());
        assertEquals(PenOrientation.DOWN, robot.getPenOrientation());
    }

    @Test
    public void historyWhenEmptyStillPrintsMessage() {
        resetAppState(3);

        String output = captureStdout(() -> App.executeCommand("h", true));
        assertTrue(output.contains("End of Command History."));
        assertEquals(0, getHistory().size());
    }

    private static void resetAppState(int size) {
        App.initialize(size);
        setStaticField(App.class, "commandHistory", new ArrayDeque<String>());
        setStaticField(App.class, "isRunning", true);
    }

    private static Robot getRobot() {
        return (Robot) getStaticField(App.class, "robot");
    }

    private static int[][] getFloor() {
        return (int[][]) getStaticField(App.class, "floor");
    }

    @SuppressWarnings("unchecked")
    private static Queue<String> getHistory() {
        return (Queue<String>) getStaticField(App.class, "commandHistory");
    }

    private static boolean getIsRunning() {
        return (boolean) getStaticField(App.class, "isRunning");
    }

    private static Object getStaticField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access " + fieldName, e);
        }
    }

    private static void setStaticField(Class<?> clazz, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set " + fieldName, e);
        }
    }

    private static void assertAllZeros(int[][] floor) {
        for (int x = 0; x < floor.length; x++) {
            for (int y = 0; y < floor[x].length; y++) {
                assertEquals(0, floor[x][y], "floor[" + x + "][" + y + "] should be 0");
            }
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
}
