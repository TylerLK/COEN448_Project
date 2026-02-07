# COEN448_Project

This repository contains the COEN 448 final project: a simple robot drawing program
implemented in Java. The project uses Maven for build and dependency management and
includes unit tests under `src/test/java`.

## Team

- Tyler Kassis - 40231047
- Sunil Kublalsingh - 40212432
- Paoleno Galvin Wendpulemdé Nikyema - 40127111
- Rahul Uresh Patel - 40030149

## Prerequisites

- Java JDK 11 or newer installed and available on `PATH`.
- Maven (3.6+) installed and available on `PATH`.
- (Optional) VS Code with the Java Extension Pack for editing and debugging.

## Build

From the repository root (the folder that contains `pom.xml`), run:

```bash
mvn compile
```

## Run (CLI)

The program's main class is `com.robot.COEN448_Project.App`.

To run from the command line:

```bash
mvn exec:java -Dexec.mainClass="com.robot.COEN448_Project.App"
```

The program reads interactive input from stdin (commands described by the
menu shown when the program starts). When running from VS Code, set the
`console` to `integratedTerminal` in the launch configuration so the program can
read user input.

## Run Tests

To execute the unit tests:

```bash
mvn test
```

Test outputs and reports will be generated in `target/surefire-reports/`.


## Running in VS Code

- Open the project folder in VS Code.
- Install the **Java Extension Pack** (if not already installed).
- Use the provided `.vscode/launch.json` configuration named `App`.
- Make sure the configuration contains `"console": "integratedTerminal"` so
	the program can read stdin from the integrated terminal.
- Start the program with **F5** (debug) or **Ctrl+F5** (run without debugging).

## Running in Eclipse

- Import the project: File > Import... > Maven > Existing Maven Projects and
	select the folder containing `pom.xml`.
- After import, right-click the project > Run As > Maven build... and use goal
	`exec:java -Dexec.mainClass="com.robot.COEN448_Project.App"`, or
- Create a Java run configuration: Run > Run Configurations... > Java
	Application, set the **Main class** to `com.robot.COEN448_Project.App`, apply,
	and Run. The Eclipse Console will accept interactive input.

## Project Structure

- `src/main/java` — application source code
- `src/test/java` — JUnit tests
- `pom.xml` — Maven project file

## AI Usage

AI Usage Claim

*Tools Used*

The primary tools used throughout the development and testing of this phase of our project (i.e., Task #1) were GitHub Copilot and Codex (OpenAI). More specifically, the primary usage of GitHub Copilot was performed through marketplace extensions on our team’s varying IDEs (e.g., VSCode, Eclipse, etc.). Furthermore, the integrated AI code review feature on our GitHub repository was employed during every Pull Request. For the latter case, the embedded GitHub version normally catches minor spelling errors or improper formatting. Finally, in terms of code generation, the two main uses for generative AI while vibe coding were for Test Case Development and Application refactoring.

### Prompts

#### Test Case Development

**Context**

You are testing a Java-based Maven project for an application that simulates a robot moving on an N×N floor grid. The system processes command-line inputs to control a robot that:
- Maintains a position (x, y) on the grid
- Maintains a direction (north, east, south, west)
- Has a pen state (up or down)
- Writes on the grid when moving with the pen down
- Stores the movement history since the program's start

The core logic is implemented in Java classes similar to:
- `Robot.java` (robot state, movement, pen, direction)
- `App.java` (command parsing, execution flow)

The floor is represented as a 2D array of integers:
- 0 = empty
- 1 = traced path (pen down)

When the print command (p) is given, wherever there is a 1 in the array, we display an asterisk; wherever there is a zero, we display a blank.

The system must correctly implement the commands:
- `[U|u]` — Pen up
- `[D|d]` — Pen down
- `[R|r]` — Turn right
- `[L|l]` — Turn left
- `[M s|m s]` — Move forward s spaces (s is a non-negative integer)
- `[P|p]` — Print the N by N array and display the indices
- `[C|c]` — Print the current position of the pen and whether it is up or down, and its facing direction
- `[Q|q]` — Stop the program
- `[I n|i n]` — Initialize the system: The values of the array floor are zeros, and the robot is back to [0, 0], pen up and facing north. n is the size of the array, an integer greater than zero
- `[H|h]` — Replay all the steps in the history since the last start of the program

By default, input of command `[M s|m s]` and `[I n|i n]` should follow the format of a command character followed by zero or one space and then an integer greater than zero.

**Objective**
Design JUnit unit tests to verify the correctness of the application functionality, focusing on:
- Robot state tracking (position, direction, pen)
- Movement logic and grid updates
- Correct handling of initialization and boundaries
- Correct output-related behaviour (state, grid content)
- Deterministic behaviour independent of command order
- Check that different types of exceptions are correctly handled, such as `ArrayIndexOutOfBoundsException` when the robot moves outside of the grid
- Input handling with multiple scenarios
- No duplicate tests

**Scope**
Include unit-level tests only targeting:

*Robot State*
- Initial state after system initialization
- Pen state transitions (up or down)
- Direction changes (left/right rotation logic)

*Movement Logic*
- Moving with pen up (no grid modification)
- Moving with pen down (grid cells set to 1)
- Multiple sequential moves
- Direction-dependent movement (north/east/south/west)
- Robot moving beyond boundaries

*Grid Behavior*
- Correct marking of the traced path
- No duplicate overwriting beyond 1
- Grid resets after re-initialization (`I n`)
- No movement outside grid bounds (or proper handling if allowed)

*Commands*
- `C`: internal state correctness (position, pen, direction)
- `P`: grid content correctness (stars vs blanks)
- `I n`: full system reset
- `M s`: handling of zero and positive step values

**Tools**
- JUnit 5
- Maven
- Standard Java assertions (`assertEquals`, `assertTrue`, `assertArrayEquals`)
- No mocking frameworks
- Tests should be runnable via `mvn test`

**Steps**
- Derive test cases per command, isolating side effects
- Use the Arrange–Act–Assert structure for every test
- Reset robot and grid state between tests
- Validate robot internal state and grid contents (2D array values)
- Ensure deterministic outcomes

**Audience**
- Testers validating Task 1 deliverables
- Teaching Assistants evaluating correctness and coverage

**Relevance**
Failure to correctly test Task 1 may result in:
- Incorrect robot movement
- Invalid traced paths
- Inconsistent grading during demo
- Poor foundation for the next step of the project (code coverage analysis)
High-quality unit tests here reduce debugging cost and increase confidence in later milestones

---------------------------------------------------end of prompt----------------------------------------------------------------

#### Application Refactoring

**Context**

- Current code: `App.java`, `Robot.java`, `App_refactored.java`
- Constraint: Refactoring should be simple and minimal.

**Outcome**

Produce an `App_refactored.java` file containing clearly denoted changes.

**Steps**

1. Read the files `../App.java` and `../Robot.java` in the Maven build project root.
2. Read the `../App_refactored.java` to verify which changes can be removed.
3. Refactor the `history()` function to optimize command recording.
4. Refactor the `print()` function to ensure that the array printing accounts for N values greater than 10.
5. Refactor the `printMenu()` function to ensure that command definitions are aligned.
6. Refactor the `executeCommand()` function to optimize command recording in the switch statement.
7. Clearly indicate with comments where the changes were made.

**Tools**

- None required.

**Audience**

- Developer implementing `App.java`.

**Relevance**

- Low-risk changes that do not affect functionality. Creates better readability and UI.

**Additional Constraints**

- Only refactor the `history()`, `print()`, `printMenu()`, & `executeCommand()` functions.
- Keep current data structures intact.
- Keep utility function generation to a minimum.



## Notes

- The `App` class validates user commands and prints user-facing error messages.
- Low-level behavior (exceptions thrown by `Robot`) is covered by unit tests in
	the test suite.
