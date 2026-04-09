package com.robot.COEN448_Project;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

public class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    public void rejectsNullAndBlankCommands() {
        assertFalse(parser.isValidCommand(null));
        assertFalse(parser.isValidCommand("   "));
    }

    @Test
    public void acceptsSingleTokenCommandsCaseInsensitively() {
        assertTrue(parser.isValidCommand("u"));
        assertTrue(parser.isValidCommand("U"));
        assertTrue(parser.isValidCommand("h"));
        assertTrue(parser.isValidCommand("Q"));
    }

    @Test
    public void validatesMoveCommands() {
        assertTrue(parser.isValidCommand("m 0"));
        assertTrue(parser.isValidCommand("M 12"));
        assertFalse(parser.isValidCommand("m -1"));
        assertFalse(parser.isValidCommand("m x"));
    }

    @Test
    public void validatesInitializeCommands() {
        assertTrue(parser.isValidCommand("i 1"));
        assertTrue(parser.isValidCommand("I 9"));
        assertFalse(parser.isValidCommand("i 0"));
        assertFalse(parser.isValidCommand("i -4"));
        assertFalse(parser.isValidCommand("i x"));
    }

    @Test
    public void rejectsUnknownOrOverSpecifiedCommands() {
        assertFalse(parser.isValidCommand("x"));
        assertFalse(parser.isValidCommand("m 1 extra"));
        assertFalse(parser.isValidCommand("u extra"));
    }

    @Test
    public void validationMessagesArePrintedForRejectedCommands() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            parser.isValidCommand("m -1");
            parser.isValidCommand("i 0");
            parser.isValidCommand("x");
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        assertTrue(output.contains("The distance must be a non-negative integer."));
        assertTrue(output.contains("The size must be a positive integer."));
        assertTrue(output.contains("Invalid Command. Please try again."));
    }
}
