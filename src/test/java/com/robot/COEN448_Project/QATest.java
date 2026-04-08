package com.robot.COEN448_Project;

import com.robot.COEN448_Project.enums.Orientation;
import com.robot.COEN448_Project.enums.PenOrientation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class QATest {

	private Robot robot;
	private int[][] standardFloor;
	private CommandParser parser;
	private CommandExecutor executor;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	public void setUp() {
		System.setOut(new PrintStream(outContent));
		robot = new Robot();
		standardFloor = new int[10][10];
		parser = new CommandParser();
		executor = new CommandExecutor(parser);
		executor.initialize(10);
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(originalOut);
	}

	// Tests transplanted from BoundaryValidationTest.java

	@Test
	public void testMoveStopsAtGridBoundary() {
		robot.move(15, standardFloor);

		assertEquals(0, robot.getX(), "X position should remain 0.");
		assertEquals(9, robot.getY(), "Y position should cap at the maximum grid index (9).");
	}

	@Test
	public void testMoveZeroStepsWithPenDown() {
		robot.penDown();

		robot.move(0, standardFloor);

		assertEquals(0, robot.getX(), "X should remain 0");
		assertEquals(0, robot.getY(), "Y should remain 0");
		assertEquals(0, standardFloor[0][0], "Floor at 0,0 should remain 0 because the loop does not execute for 0 steps.");
	}

	@Test
	public void testMoveNegativeStepsThrowsException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> robot.move(-3, standardFloor));
		assertEquals("Steps must be a non-negative integer.", exception.getMessage());
	}

	@Test
	public void testMoveOnMinimumValidGrid() {
		int[][] tinyFloor = new int[1][1];
		robot.penDown();

		robot.move(1, tinyFloor);

		assertEquals(0, robot.getX(), "X should remain 0 on a 1x1 grid.");
		assertEquals(0, robot.getY(), "Y should remain 0 on a 1x1 grid.");
	}

	@Test
	public void testInvalidGridInitializationBounds() {
		assertFalse(parser.isValidCommand("I 0"), "Grid size 0 should be rejected.");
		assertFalse(parser.isValidCommand("I -5"), "Negative grid sizes should be rejected.");
	}

	@Test
	public void testValidMinimumGridInitialization() {
		assertTrue(parser.isValidCommand("I 1"), "Grid size 1x1 should be accepted.");
	}

	@Test
	public void testInvalidMovementBounds() {
		assertFalse(parser.isValidCommand("M -5"), "Negative movement steps should be rejected by the parser.");
	}

	@Test
	public void testValidZeroMovement() {
		assertTrue(parser.isValidCommand("M 0"), "Movement of 0 steps should be accepted as valid syntax.");
	}

	// Tests transplanted from FunctionalBlackBoxTest.java

	@Test
	public void testPenStateTransitions() {
		assertEquals(PenOrientation.UP, robot.getPenOrientation(), "Pen should start UP.");

		robot.penDown();
		assertEquals(PenOrientation.DOWN, robot.getPenOrientation(), "Pen should be DOWN after penDown().");

		robot.penUp();
		assertEquals(PenOrientation.UP, robot.getPenOrientation(), "Pen should be UP after penUp().");
	}

	@Test
	public void testTurnRightCyclesCorrectly() {
		assertEquals(Orientation.NORTH, robot.getDirection());

		robot.turnRight();
		assertEquals(Orientation.EAST, robot.getDirection());

		robot.turnRight();
		assertEquals(Orientation.SOUTH, robot.getDirection());

		robot.turnRight();
		assertEquals(Orientation.WEST, robot.getDirection());

		robot.turnRight();
		assertEquals(Orientation.NORTH, robot.getDirection());
	}

	@Test
	public void testTurnLeftCyclesCorrectly() {
		assertEquals(Orientation.NORTH, robot.getDirection());

		robot.turnLeft();
		assertEquals(Orientation.WEST, robot.getDirection());

		robot.turnLeft();
		assertEquals(Orientation.SOUTH, robot.getDirection());

		robot.turnLeft();
		assertEquals(Orientation.EAST, robot.getDirection());

		robot.turnLeft();
		assertEquals(Orientation.NORTH, robot.getDirection());
	}

	@Test
	public void testMoveWithPenUpDoesNotMarkFloor() {
		robot.move(3, standardFloor);

		assertEquals(0, robot.getX(), "X should remain 0");
		assertEquals(3, robot.getY(), "Y should be 3 after moving NORTH");

		for (int i = 0; i <= 3; i++) {
			assertEquals(0, standardFloor[0][i], "Floor should remain 0 when pen is UP.");
		}
	}

	@Test
	public void testMoveWithPenDownMarksPath() {
		robot.penDown();
		robot.move(2, standardFloor);

		assertEquals(0, robot.getX());
		assertEquals(2, robot.getY());

		assertEquals(1, standardFloor[0][0], "Origin should be marked 1.");
		assertEquals(1, standardFloor[0][1], "Path tile [0,1] should be marked 1.");
		assertEquals(1, standardFloor[0][2], "Destination tile [0,2] should be marked 1.");
		assertEquals(0, standardFloor[0][3], "Tiles beyond movement should remain 0.");
	}

	@Test
	public void testPrintPositionCommand() {
		executor.executeCommand("R", true);
		executor.executeCommand("M 2", true);

		outContent.reset();

		executor.executeCommand("C", true);

		String output = outContent.toString();
		assertTrue(output.contains("Position: 2, 0 - Pen: UP - Facing: EAST"),
			"The C command should print the updated coordinates, pen state, and direction.");
	}

	@Test
	public void testInitializeCommandResetsSystem() {
		executor.executeCommand("D", true);
		executor.executeCommand("R", true);
		executor.executeCommand("M 4", true);

		executor.executeCommand("I 8", true);

		outContent.reset();
		executor.executeCommand("C", true);
		String output = outContent.toString();

		assertTrue(output.contains("Position: 0, 0 - Pen: UP - Facing: NORTH"),
			"The I command should reset the robot to [0,0], UP, and NORTH.");
	}

	@Test
	public void testPrintFloorCommand() {
		executor.executeCommand("D", true);
		executor.executeCommand("M 2", true);

		outContent.reset();

		executor.executeCommand("P", true);

		String output = outContent.toString();
		assertTrue(output.contains("*"), "The P command should print the floor and display asterisks for marked tiles.");
	}

	@Test
	public void testHistoryCommandReplaysSteps() {
		executor.executeCommand("I 5", true);
		executor.executeCommand("R", true);
		executor.executeCommand("M 1", true);

		outContent.reset();

		executor.executeCommand("H", true);

		String output = outContent.toString();
		assertTrue(output.contains("End of Command History."),
			"The H command should replay all steps and print the completion message.");
	}

	// Tests transplanted from WhiteBoxCoverageTest.java

	@Test
	public void testIsValidCommand_NullCondition() {
		assertFalse(parser.isValidCommand(null), "Null command should return false.");
	}

	@Test
	public void testIsValidCommand_EmptyCondition() {
		assertFalse(parser.isValidCommand("   "), "Blank command should return false.");
	}

	@Test
	public void testShouldAddToHistory_AllTrue() {
		executor.executeCommand("u", true);
	}

	@Test
	public void testShouldAddToHistory_FirstConditionFalse() {
		executor.executeCommand("u", false);
	}

	@Test
	public void testShouldAddToHistory_SecondConditionFalse() {
		executor.executeCommand("q", true);
	}

	@Test
	public void testShouldAddToHistory_ThirdConditionFalse() {
		executor.executeCommand("h", true);
	}

	@Test
	public void testShouldAddToHistory_FirstAndSecondConditionFalse() {
		executor.executeCommand("q", false);
	}

	@Test
	public void testIsValidCommand_TooManyArgumentsBranch() {
		assertFalse(parser.isValidCommand("M 5 extra"), "Should reject > 2 tokens.");
	}

	@Test
	public void testIsValidCommand_SingleTokenBranch() {
		assertTrue(parser.isValidCommand("U"), "Should accept valid single token.");
		assertFalse(parser.isValidCommand("U 5"), "Should reject single token command with extra args.");
	}

	@Test
	public void testIsValidCommand_SwitchBranchM() {
		assertTrue(parser.isValidCommand("M 5"), "Should accept valid M command.");
		assertFalse(parser.isValidCommand("M string"), "Should reject non-integer M command.");
	}

	@Test
	public void testIsValidCommand_SwitchBranchI() {
		assertTrue(parser.isValidCommand("I 10"), "Should accept valid I command.");
		assertFalse(parser.isValidCommand("I string"), "Should reject non-integer I command.");
	}

	@Test
	public void testIsValidCommand_SwitchBranchDefault() {
		assertFalse(parser.isValidCommand("X"), "Should reject unknown command.");
		assertFalse(parser.isValidCommand("X 5"), "Should reject unknown command with args.");
	}
}
