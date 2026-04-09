package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class AppTest {

    /** Instantiates App to cover the implicit default constructor reported uncovered by JaCoCo. */
    @Test
    public void appDefaultConstructorIsInstantiable() {
        assertNotNull(new App());
    }

    @Test
    public void mainDelegatesToSimulationAndExits() {
        InputOutputContext context = new InputOutputContext("1\nq\n");

        try {
            assertDoesNotThrow(() -> App.main(new String[0]));
        } finally {
            context.restore();
        }

        String output = context.getOutput();
        assertTrue(output.contains("Please enter the size you would like the floor to be (N x N):"));
        assertTrue(output.contains("Available Commands:"));
        assertTrue(output.contains("Exiting Program. Goodbye!"));
    }

    private static final class InputOutputContext {
        private final PrintStream originalOut = System.out;
        private final java.io.InputStream originalIn = System.in;
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        private InputOutputContext(String input) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(out));
        }

        private String getOutput() {
            return out.toString();
        }

        private void restore() {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
