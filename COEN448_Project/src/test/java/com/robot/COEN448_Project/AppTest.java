package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void constructorCanBeInvoked() {
        App app = new App();
        assertTrue(app != null);
    }

    @Test
    public void initializeCreatesFloorAndSetsRobotStartPosition() throws Exception {
        App.Initialize(4);

        char[][] floor = getFloor();
        int[][] floorStatus = getFloorStatus();

        assertEquals(4, floor.length);
        assertEquals(4, floorStatus.length);
        assertEquals(0, getIntField("robotPositionX"));
        assertEquals(3, getIntField("robotPositionY"));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(' ', floor[i][j]);
                assertEquals(0, floorStatus[i][j]);
            }
        }
    }

    @Test
    public void initializeResetsPreviousState() throws Exception {
        App.Initialize(2);
        char[][] floor = getFloor();
        int[][] floorStatus = getFloorStatus();
        floor[1][1] = '*';
        floorStatus[0][0] = 1;

        App.Initialize(3);

        floor = getFloor();
        floorStatus = getFloorStatus();
        assertEquals(3, floor.length);
        assertEquals(3, floorStatus.length);
        assertEquals(' ', floor[1][1]);
        assertEquals(0, floorStatus[0][0]);
        assertEquals(0, getIntField("robotPositionX"));
        assertEquals(2, getIntField("robotPositionY"));
    }

    @Test
    public void printOutputsCurrentFloorState() throws Exception {
        App.Initialize(3);
        char[][] floor = getFloor();
        floor[1][2] = '*';

        String output = captureStdOut(App::Print);
        String lineSeparator = System.lineSeparator();

        assertEquals(
                "   " + lineSeparator
                        + "  *" + lineSeparator
                        + "   " + lineSeparator,
                output);
    }

    @Test
    public void mainReadsInputAndPrintsFloor() {
        InputStream originalIn = System.in;
        ByteArrayInputStream testIn = new ByteArrayInputStream("3\n".getBytes(StandardCharsets.UTF_8));

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setIn(testIn);
            System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
            App.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Please enter the size you would like the floor to be (N x N): "));
        assertTrue(output.contains("  *" + System.lineSeparator()));
    }

    private static String captureStdOut(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private static char[][] getFloor() throws Exception {
        return (char[][]) getField("floor");
    }

    private static int[][] getFloorStatus() throws Exception {
        return (int[][]) getField("floorStatus");
    }

    private static int getIntField(String fieldName) throws Exception {
        return (int) getField(fieldName);
    }

    private static Object getField(String fieldName) throws Exception {
        Field field = App.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }
}
