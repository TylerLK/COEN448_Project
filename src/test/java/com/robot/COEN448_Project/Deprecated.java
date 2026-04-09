package com.robot.COEN448_Project;

/**
 * Deprecated regression tests retained for reference after the class split.
 * All test methods in this file are intentionally commented out.
 */
final class Deprecated {
    private Deprecated() {
    }

    /*
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

        executor.executeCommand("D", true);
        executor.executeCommand("m 2", true);

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
    void moveOutsideGridStopsAtBoundary() {
        int[][] floor = new int[2][2];
        Robot robot = new Robot();

        robot.penDown();

        robot.move(5, floor);
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());
        assertEquals(2, countMarks(floor));
        assertEquals(1, floor[0][0]);
        assertEquals(1, floor[0][1]);
    }

    @Test
    public void executeCommandPenUpAndDownAffectsRobot() {
        resetAppState(3);

        executor.executeCommand("d", true);
        assertEquals(PenOrientation.DOWN, getRobot().getPenOrientation());

        executor.executeCommand("u", true);
        assertEquals(PenOrientation.UP, getRobot().getPenOrientation());
    }

    @Test
    public void executeCommandTurnsUpdateDirection() {
        resetAppState(3);

        executor.executeCommand("r", true);
        assertEquals(Orientation.EAST, getRobot().getDirection());

        executor.executeCommand("l", true);
        assertEquals(Orientation.NORTH, getRobot().getDirection());
    }

    @Test
    public void executeCommandMoveZeroDoesNotMarkOrMove() {
        resetAppState(3);

        executor.executeCommand("d", true);
        executor.executeCommand("m 0", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(0, countMarks(getFloor()));
    }

    @Test
    public void executeCommandNegativeMovePrintsInvalidCommand() {
        resetAppState(3);

        String output = captureStdout(() -> executor.executeCommand("m -1", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));
    }

    @Test
    public void executeCommandTrimsInputAndAcceptsUppercase() {
        resetAppState(3);

        executor.executeCommand("  d  ", true);
        executor.executeCommand("  M 1  ", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(1, robot.getY());
        assertEquals(2, countMarks(getFloor()));
    }

    @Test
    public void executeCommandInvalidCommandPrintsMessage() {
        resetAppState(3);

        String output = captureStdout(() -> executor.executeCommand("x", true));
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
        String output = captureStdout(() -> executor.executeCommand("m", true));
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
        String output = captureStdout(() -> executor.executeCommand("i", true));
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
        String output = captureStdout(() -> executor.executeCommand("m x", true));
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

        String emptyOutput = captureStdout(() -> executor.executeCommand("", true));
        assertTrue(emptyOutput.contains("Empty Command. Please try again."));

        String blankOutput = captureStdout(() -> executor.executeCommand("   ", true));
        assertTrue(blankOutput.contains("Empty Command. Please try again."));
    }

    @Test
    public void executeCommandNullCommandThrows() {
        resetAppState(3);
        String output = captureStdout(() -> executor.executeCommand(null, true));
        assertTrue(output.contains("Empty Command. Please try again."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(PenOrientation.UP, robot.getPenOrientation());
        assertEquals(Orientation.NORTH, robot.getDirection());
    }

    @Test
    public void executeCommandInitWithExtraArgsDoesNotChangeAppState() {
        resetAppState(3);

        executor.executeCommand("i 4 extra", true);

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
        String output = captureStdout(() -> executor.executeCommand("i x", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));
    }

    @Test
    public void executeCommandNegativeInitArgumentThrows() {
        resetAppState(3);
        String output = captureStdout(() -> executor.executeCommand("i -1", true));
        assertTrue(output.contains("Invalid Command. The size must be a positive integer."));
    }

    @Test
    public void executeCommandZeroInitCreatesEmptyFloorAndResetsRobot() {
        resetAppState(3);
        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);

        executor.executeCommand("i 1", true);

        String output = captureStdout(() -> executor.executeCommand("i 0", true));
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
        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);

        String output = captureStdout(() -> executor.executeCommand("c", true));
        assertTrue(output.contains("Position: 0, 1 - Pen: DOWN - Facing: NORTH"));
    }

    @Test
    public void executeCommandPrintRendersStars() {
        resetAppState(2);
        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);

        String output = captureStdout(() -> executor.executeCommand("p", true));
        long starCount = output.chars().filter(ch -> ch == '*').count();
        assertEquals(2L, starCount);

        String[] lines = output.split("\\R");
        String bottomIndexLine = null;
        for (int i = lines.length - 1; i >= 0; i--) {
            if (!lines[i].trim().isEmpty()) {
                bottomIndexLine = lines[i];
                break;
            }
        }
        assertNotNull(bottomIndexLine, "Expected a bottom index line to be printed");
    }

    @Test
    public void executeCommandQuitStopsProgram() {
        resetAppState(2);

        String output = captureStdout(() -> executor.executeCommand("q", true));
        assertFalse(getIsRunning());
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    @Test
    public void initializeCommandResetsGridAndRobot() {
        resetAppState(4);
        executor.executeCommand("d", true);
        executor.executeCommand("m 3", true);

        executor.executeCommand("i 2", true);

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

        executor.executeCommand("d", false);
        Queue<String> history = getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void historyReplaysCommandsAndPreservesQueue() {
        resetAppState(4);

        executor.executeCommand("d", true);
        executor.executeCommand("m 1", true);
        executor.executeCommand("r", true);
        executor.executeCommand("m 1", true);

        Queue<String> before = new ArrayDeque<>(getHistory());

        String output = captureStdout(() -> executor.executeCommand("h", true));
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
    }

    @Test
    public void historyWhenEmptyStillPrintsMessage() {
        resetAppState(3);

        String output = captureStdout(() -> executor.executeCommand("h", true));
        assertTrue(output.contains("End of Command History."));
        assertEquals(0, getHistory().size());
    }

    @Test
    public void printMenuContainsAllCommands() {
        String output = captureStdout(() -> controller.printMenu());
        assertTrue(output.contains("Available Commands:"));
        assertTrue(output.contains("[U|u]      |  Pen Up"));
        assertTrue(output.contains("[D|d]      |  Pen Down"));
        assertTrue(output.contains("[R|r]      |  Turn Right"));
        assertTrue(output.contains("[L|l]      |  Turn Left"));
        assertTrue(output.contains("[M s|m s]  |  Move Forward s Spaces (s = Non-negative Integer)"));
        assertTrue(output.contains("[P|p]      |  Print the Floor"));
        assertTrue(output.contains("[C|c]      |  Print the Robot's Current Position and Direction"));
        assertTrue(output.contains("[I n|i n]  |  Initialize the System with a New Floor of Size n x n (n = Positive Integer)"));
        assertTrue(output.contains("[H|h]      |  Replay Command History"));
        assertTrue(output.contains("[Q|q]      |  Stop the Program"));
    }

    @Test
    public void isValidCommandRejectsInvalidSyntax() {
        resetAppState(3);

        String output1 = captureStdout(() -> executor.executeCommand("x", true));
        assertTrue(output1.contains("Invalid Command. Please try again."));

        String output2 = captureStdout(() -> executor.executeCommand("xyz", true));
        assertTrue(output2.contains("Invalid Command. Please try again."));

        String output3 = captureStdout(() -> executor.executeCommand("123", true));
        assertTrue(output3.contains("Invalid Command. Please try again."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    public void isValidCommandRejectsNegativeMove() {
        resetAppState(3);

        String output = captureStdout(() -> executor.executeCommand("m -5", true));
        assertTrue(output.contains("Invalid Command. The distance must be a non-negative integer."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    public void isValidCommandRejectsMissingMoveArgument() {
        resetAppState(3);

        String output = captureStdout(() -> executor.executeCommand("m", true));
        assertTrue(output.contains("Invalid Command. Incorrect number of arguments for this command."));

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    public void isValidCommandRejectsTooManyArguments() {
        resetAppState(3);

        String output1 = captureStdout(() -> executor.executeCommand("m 5 10", true));
        assertTrue(output1.contains("Invalid Command. Too many arguments"));

        String output2 = captureStdout(() -> executor.executeCommand("u extra", true));
        assertTrue(output2.contains("Invalid Command. Too many arguments"));

        String output3 = captureStdout(() -> executor.executeCommand("d extra args", true));
        assertTrue(output3.contains("Invalid Command. Too many arguments"));
    }

    @Test
    public void robotMoveWithBoundsCheckStopsAtEdge() {
        resetAppState(3);

        executor.executeCommand("d", true);
        executor.executeCommand("m 100", true);

        Robot robot = getRobot();
        assertEquals(0, robot.getX());
        assertEquals(2, robot.getY());
        assertTrue(robot.getY() < 3);
    }

    @Test
    public void executeCommandSwitchHandlesIllegalArgumentException() {
        resetAppState(3);

        Robot throwingRobot = new Robot() {
            @Override
            public void move(int steps, int[][] floor) {
                throw new IllegalArgumentException("Test exception from Robot.move()");
            }
        };

        setFieldValue("robot", throwingRobot);

        String output = captureStdout(() -> executor.executeCommand("m 5", false));
        assertTrue(output.contains("Test exception from Robot.move()"));
    }

    @Test
    public void executeCommandSwitchHandlesArrayIndexOutOfBoundsException() {
        resetAppState(3);

        String output = captureStdout(() -> {
            try {
                String[] commandTokens = new String[]{"m"};
                int[][] floor = getFloor();
                Robot robot = getRobot();
                try {
                    robot.move(Integer.parseInt(commandTokens[1]), floor);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            } catch (Exception ignored) {
                System.out.println("Index 1 out of bounds for length 1");
            }
        });

        assertTrue(output.contains("1") || output.contains("Index") || output.contains("bounds"));
    }

    @Test
    public void executeCommandSwitchDefaultCase() {
        resetAppState(3);

        String output = captureStdout(() -> {
            String caseBlindCommand = "unrecognized";

            switch (caseBlindCommand) {
                case "u":
                case "d":
                case "r":
                case "l":
                case "m":
                case "p":
                case "c":
                case "q":
                case "i":
                case "h":
                    break;
                default:
                    System.out.println("Invalid Command. Please try again.");
                    break;
            }
        });

        assertTrue(output.contains("Invalid Command. Please try again."));
    }

    private void resetAppState(int size) {
        executor.initialize(size);
        setFieldValue("commandHistory", new ArrayDeque<String>());
        setFieldValue("isRunning", true);
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

    private boolean getIsRunning() {
        return (boolean) getFieldValue("isRunning");
    }

    private Object getFieldValue(String fieldName) {
        try {
            Field field = CommandExecutor.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(executor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access " + fieldName, e);
        }
    }

    private void setFieldValue(String fieldName, Object value) {
        try {
            Field field = CommandExecutor.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(executor, value);
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
    */
}
