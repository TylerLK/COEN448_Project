package com.robot.COEN448_Project;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void executeCommandTurnLeftUpdatesDirection() {
        executor.executeCommand("l", true);
        assertEquals(Orientation.WEST, getRobot().getDirection());
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

    // ─── TC-IV-01b to TC-IV-03b, TC-IV-09, TC-IV-10, TC-IV-15 ───────────────

    /**
     * TC-IV-01b: "m  5" (double space) – split("\\s+") tokenises correctly,
     * command accepted, robot moves to (0,5).
     */
    @Test
    public void tcIv01bDoubleSpaceAccepted() {
        executor.initialize(10);
        executor.executeCommand("m  5", true);
        assertEquals(0, getRobot().getX());
        assertEquals(5, getRobot().getY());
    }

    /** TC-IV-02b: "m\t5" (tab separator) – accepted, robot moves to (0,5). */
    @Test
    public void tcIv02bTabSeparatorAccepted() {
        executor.initialize(10);
        executor.executeCommand("m\t5", true);
        assertEquals(0, getRobot().getX());
        assertEquals(5, getRobot().getY());
    }

    /** TC-IV-03b: "  m   5  " (leading/trailing whitespace) – accepted, robot moves to (0,5). */
    @Test
    public void tcIv03bLeadingTrailingWhitespaceAccepted() {
        executor.initialize(10);
        executor.executeCommand("  m   5  ", true);
        assertEquals(0, getRobot().getX());
        assertEquals(5, getRobot().getY());
    }

    /**
     * TC-IV-09: "m -0" – Integer.parseInt("-0") == 0, accepted by parser.
     * Pen set DOWN first; assert no marks and robot stays at (0,0).
     */
    @Test
    public void tcIv09NegativeZeroTreatedAsZeroStep() {
        executor.executeCommand("d", true);   // pen DOWN so marks would appear on any actual movement
        executor.executeCommand("m -0", true);

        assertEquals(0, getRobot().getX(), "Robot x should remain 0");
        assertEquals(0, getRobot().getY(), "Robot y should remain 0");
        assertEquals(0, countMarks(getFloor()), "No floor cells should be marked after 0-step move");
    }

    /** TC-IV-10: "m +5" – Integer.parseInt("+5") == 5, accepted; robot moves to (0,5). */
    @Test
    public void tcIv10PlusPrefixAccepted() {
        executor.initialize(10);
        executor.executeCommand("m +5", true);
        assertEquals(0, getRobot().getX());
        assertEquals(5, getRobot().getY());
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

        assertEquals(0,                    getRobot().getX(),              "Robot x should be 0");
        assertEquals(0,                    getRobot().getY(),              "Robot y should be 0");
        assertEquals(PenOrientation.UP,    getRobot().getPenOrientation(), "Pen should be UP");
        assertEquals(Orientation.NORTH,    getRobot().getDirection(),      "Robot should face NORTH");
        assertEquals(0,                    countMarks(getFloor()),         "No floor cells should be marked");
    }

    /**
     * Covers the catch (IllegalArgumentException e) block in the "m" switch case.
     * isValidCommand normally prevents negatives, so we inject a Robot subclass
     * that throws unconditionally to exercise the defensive catch path.
     */
    @Test
    public void moveCommandCatchesIllegalArgumentExceptionFromRobot() {
        CommandExecutor exec = new CommandExecutor(new CommandParser());
        exec.initialize(5);
        setFieldValue(exec, "robot", new Robot() {
            @Override
            public void move(int steps, int[][] floor) {
                throw new IllegalArgumentException("Simulated IAE from Robot.move()");
            }
        });

        String output = captureStdout(() -> exec.executeCommand("m 5", true));
        assertTrue(output.contains("Simulated IAE from Robot.move()"));
    }

    /**
     * Covers the default case in the executeCommand switch.
     * The case is unreachable through isValidCommand, so we supply an
     * anonymous CommandParser that always returns true, then send a command
     * whose first token matches no case label.
     */
    @Test
    public void switchDefaultCasePrintsInvalidCommandMessage() {
        CommandParser alwaysValid = new CommandParser() {
            @Override
            public boolean isValidCommand(String command) {
                return true;
            }
        };
        CommandExecutor exec = new CommandExecutor(alwaysValid);
        exec.initialize(5);

        String output = captureStdout(() -> exec.executeCommand("z", true));
        assertTrue(output.contains("Invalid Command. Please try again."));
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
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
        for (int[] col : floor) {
            for (int cell : col) {
                if (cell == 1) {
                    count++;
                }
            }
        }
        return count;
    }
}
