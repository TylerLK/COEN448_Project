package com.robot.COEN448_Project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WhiteBoxCoverageTest {

    @BeforeEach
    public void setUp() {
        // Initialize standard floor to avoid NullPointerExceptions
        App.initialize(10); 
    }

    // ==========================================
    // CONDITION & MULTIPLE CONDITION COVERAGE
    // ==========================================
    
    /* * Target 1: isValidCommand(String command)
     * Line: if (command == null || command.trim().isEmpty())
     * We need to test the combinations of this OR (||) condition.
     */
    
    @Test
    public void testIsValidCommand_NullCondition() {
        // Tests: command == null (TRUE). Short-circuits the OR.
        assertFalse(App.isValidCommand(null), "Null command should return false.");
    }

    @Test
    public void testIsValidCommand_EmptyCondition() {
        // Tests: command == null (FALSE) || command.trim().isEmpty() (TRUE)
        assertFalse(App.isValidCommand("   "), "Blank command should return false.");
    }

    /* * Target 2: executeCommand(String command, boolean addToHistory)
     * Line: boolean shouldAddToHistory = addToHistory && !caseBlindCommand.equals("q") && !caseBlindCommand.equals("h");
     * We must test multiple combinations of these three AND (&&) conditions.
     */

    @Test
    public void testShouldAddToHistory_AllTrue() {
        // Condition: True && True (!= q) && True (!= h)
        // Action: Execute a standard command with flag set to true
        App.executeCommand("u", true);
        // If successful, this adds to the internal commandHistory queue.
    }

    @Test
    public void testShouldAddToHistory_FirstConditionFalse() {
        // Condition: False && (Short-circuits)
        // Action: Execute standard command with flag set to false
        App.executeCommand("u", false);
    }

    @Test
    public void testShouldAddToHistory_SecondConditionFalse() {
        // Condition: True && False (is "q") && (Short-circuits)
        // Action: Execute quit command. Note: This sets isRunning to false internally.
        App.executeCommand("q", true); 
    }

    @Test
    public void testShouldAddToHistory_ThirdConditionFalse() {
        // Condition: True && True (!= q) && False (is "h")
        // Action: Execute history command
        App.executeCommand("h", true);
    }

    // ==========================================
    // DECISION (BRANCH) COVERAGE
    // ==========================================

    /*
     * Target 3: isValidCommand(String command) -> switch(caseBlindCommand)
     * We must hit every branch of the switch statement and the > 2 length check.
     */

    @Test
    public void testIsValidCommand_TooManyArgumentsBranch() {
        // Hits the branch: if (commandTokens.length > 2)
        assertFalse(App.isValidCommand("M 5 extra"), "Should reject > 2 tokens.");
    }

    @Test
    public void testIsValidCommand_SingleTokenBranch() {
        // Hits the branch: if (isSingleTokenCommand(caseBlindCommand))
        assertTrue(App.isValidCommand("U"), "Should accept valid single token.");
        assertFalse(App.isValidCommand("U 5"), "Should reject single token command with extra args.");
    }

    @Test
    public void testIsValidCommand_SwitchBranchM() {
        // Hits switch case "m" and valid/invalid integer parsing
        assertTrue(App.isValidCommand("M 5"), "Should accept valid M command.");
        assertFalse(App.isValidCommand("M string"), "Should reject non-integer M command.");
    }

    @Test
    public void testIsValidCommand_SwitchBranchI() {
        // Hits switch case "i" and valid/invalid integer parsing
        assertTrue(App.isValidCommand("I 10"), "Should accept valid I command.");
        assertFalse(App.isValidCommand("I string"), "Should reject non-integer I command.");
    }

    @Test
    public void testIsValidCommand_SwitchBranchDefault() {
        // Hits the default switch case (unrecognized command)
        assertFalse(App.isValidCommand("X"), "Should reject unknown command.");
        assertFalse(App.isValidCommand("X 5"), "Should reject unknown command with args.");
    }
}