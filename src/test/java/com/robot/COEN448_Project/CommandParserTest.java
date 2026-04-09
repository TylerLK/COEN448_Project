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

    // ─── TC-IV-04 to TC-IV-14: input validation edge cases ───────────────────

    /**
     * "m" alone (no argument) – triggers the commandTokens.length != 2 branch
     * inside isValidDistanceCommand, distinct from the > 2 early-exit path.
     */
    @Test
    public void rejectsMoveCommandWithMissingArgument() {
        String output = captureStdout(() -> parser.isValidCommand("m"));
        assertFalse(parser.isValidCommand("m"));
        assertTrue(output.contains("Incorrect number of arguments for this command."));
    }

    /**
     * "i" alone (no argument) – triggers the commandTokens.length != 2 branch
     * inside isValidInitializeCommand, distinct from the > 2 early-exit path.
     */
    @Test
    public void rejectsInitCommandWithMissingArgument() {
        String output = captureStdout(() -> parser.isValidCommand("i"));
        assertFalse(parser.isValidCommand("i"));
        assertTrue(output.contains("Incorrect number of arguments for this command."));
    }

    /** TC-IV-04: "m 2147483648" – integer overflow rejected with distance error. */
    @Test
    public void tcIv04IntegerOverflowInMoveRejected() {
        String output = captureStdout(() -> parser.isValidCommand("m 2147483648"));
        assertFalse(parser.isValidCommand("m 2147483648"));
        assertTrue(output.contains("The distance must be a non-negative integer."));
    }

    /** TC-IV-05: "i 2147483648" – integer overflow rejected with size error. */
    @Test
    public void tcIv05IntegerOverflowInInitRejected() {
        String output = captureStdout(() -> parser.isValidCommand("i 2147483648"));
        assertFalse(parser.isValidCommand("i 2147483648"));
        assertTrue(output.contains("The size must be a positive integer."));
    }

    /** TC-IV-06: "m 3.5" – float format rejected with distance error. */
    @Test
    public void tcIv06FloatMoveRejected() {
        String output = captureStdout(() -> parser.isValidCommand("m 3.5"));
        assertFalse(parser.isValidCommand("m 3.5"));
        assertTrue(output.contains("The distance must be a non-negative integer."));
    }

    /** TC-IV-07: "i 4.0" – float format rejected with size error. */
    @Test
    public void tcIv07FloatInitRejected() {
        String output = captureStdout(() -> parser.isValidCommand("i 4.0"));
        assertFalse(parser.isValidCommand("i 4.0"));
        assertTrue(output.contains("The size must be a positive integer."));
    }

    /** TC-IV-08: "m 1e5" – scientific notation rejected with distance error. */
    @Test
    public void tcIv08ScientificNotationMoveRejected() {
        String output = captureStdout(() -> parser.isValidCommand("m 1e5"));
        assertFalse(parser.isValidCommand("m 1e5"));
        assertTrue(output.contains("The distance must be a non-negative integer."));
    }

    /** TC-IV-11: "x 5" – unknown two-token command rejected with generic error. */
    @Test
    public void tcIv11UnknownTwoTokenCommandRejected() {
        String output = captureStdout(() -> parser.isValidCommand("x 5"));
        assertFalse(parser.isValidCommand("x 5"));
        assertTrue(output.contains("Invalid Command. Please try again."));
    }

    /** TC-IV-12: "@", "!", "#5" – each rejected with generic invalid-command error. */
    @Test
    public void tcIv12SpecialCharacterCommandsRejected() {
        assertFalse(parser.isValidCommand("@"));
        assertFalse(parser.isValidCommand("!"));
        assertFalse(parser.isValidCommand("#5"));

        String out1 = captureStdout(() -> parser.isValidCommand("@"));
        assertTrue(out1.contains("Invalid Command. Please try again."), "@ should be rejected");

        String out2 = captureStdout(() -> parser.isValidCommand("!"));
        assertTrue(out2.contains("Invalid Command. Please try again."), "! should be rejected");

        String out3 = captureStdout(() -> parser.isValidCommand("#5"));
        assertTrue(out3.contains("Invalid Command. Please try again."), "#5 should be rejected");
    }

    /** TC-IV-13: "m 0x10" – hexadecimal notation rejected with distance error. */
    @Test
    public void tcIv13HexNotationMoveRejected() {
        String output = captureStdout(() -> parser.isValidCommand("m 0x10"));
        assertFalse(parser.isValidCommand("m 0x10"));
        assertTrue(output.contains("The distance must be a non-negative integer."));
    }

    /** TC-IV-14: "\t" and "\n" – whitespace-only inputs rejected with empty-command error. */
    @Test
    public void tcIv14WhitespaceOnlyInputsRejected() {
        assertFalse(parser.isValidCommand("\t"));
        assertFalse(parser.isValidCommand("\n"));

        String out1 = captureStdout(() -> parser.isValidCommand("\t"));
        assertTrue(out1.contains("Empty Command. Please try again."), "Tab-only should be rejected");

        String out2 = captureStdout(() -> parser.isValidCommand("\n"));
        assertTrue(out2.contains("Empty Command. Please try again."), "Newline-only should be rejected");
    }

    private static String captureStdout(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf));
        try {
            action.run();
            return buf.toString();
        } finally {
            System.setOut(originalOut);
        }
    }
}
