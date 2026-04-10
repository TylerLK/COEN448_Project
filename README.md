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

## Code Coverage (Jacoco)

To generate code coverage reports:

```bash
mvn clean test jacoco:report
```

Coverage reports will be generated in `target/site/jacoco/`.

## Static Analysis (SonarQube)

To perform static code analysis using SonarQube:

### 1. Start SonarQube
If you have Docker Desktop installed, run:
```bash
docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community
```

### 2. Configure SonarQube
1. Open [http://localhost:9000](http://localhost:9000) (User: `admin`, Pass: `admin`).
2. Follow prompts to change the password.
3. Follow the steps for creating a new project manually called COEN448_Project (must be called this) and then follow the steps for generating a token with no expiration date.

### 3. Run Analysis (via Docker)
Run the following command from the project root. This command uses a Dockerized Maven environment, ensuring consistent results across Windows and macOS:

**macOS / Linux / Windows (PowerShell):**
```bash
docker run --rm -v "${PWD}:/usr/src/mymaven" -w /usr/src/mymaven maven:3.8-openjdk-17-slim mvn verify sonar:sonar -Dsonar.projectKey=COEN448_Project -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.login=YOUR_TOKEN
```
*(Note: `${PWD}` automatically detects your current folder, so this command works no matter where you saved the project.)*

**Windows (Git Bash):**
Git Bash requires a special flag to prevent it from mangling paths. Use this exact command and replace YOUR_TOKEN with your actual SonarQube token:
```bash
MSYS_NO_PATHCONV=1 docker run --rm -v "${PWD}:/usr/src/mymaven" -w /usr/src/mymaven maven:3.8-openjdk-17-slim mvn verify sonar:sonar -Dsonar.projectKey=COEN448_Project -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.login=YOUR_TOKEN
```
*(Note: If you still see "Missing Project" errors, double-check that you are in the folder containing `pom.xml`.)*

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

The primary tools used throughout the development and testing of this phase of our project (i.e., Task #1) were GitHub Copilot and Codex (OpenAI). More specifically, the primary usage of GitHub Copilot was performed through marketplace extensions on our team’s varying IDEs (e.g., VSCode, Eclipse, etc.). Furthermore, the integrated AI code review feature on our GitHub repository was employed during every Pull Request. For the latter case, the embedded GitHub version normally catches minor spelling errors or improper formatting. Finally, in terms of code generation, the two main uses for generative AI while vibe coding were for test case development and targeted application refactoring

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

---

<p align="center"><em>End of prompt</em></p>

---

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


---
<p align="center"><em>End of prompt</em></p>

---

#### SonarQube isValidCommand() Complexity Reduction

Refactor the isValidCommand to reduce its Cognitive Complexity from 21 to the 15 allowed.

---
<p align="center"><em>End of prompt</em></p>

---

#### Additional test case for the printMenu() method

**COSTAR Prompt**

**Context:**
You are provided with the current code of the App.java and AppTest.java 

**Outcome:**
We need to write a test case in AppTest.java to test efficiently the printMenu() method implemented in App.java

**Steps:**
1. Read the files App.java and AppTest.java completely
2. Add a minimal number of test cases to test everything related to the printMenu() method
3. Check the final list of tests to ensure you do not have duplicates. If duplicate tests exist, report them in the conversation and remove them to avoid redundancy.

**Tools:**
- Junit 5

**Audience:**
- Teaching Assistants evaluating correctness and coverage

**Relevance:**
The method needs to be fully tested to ensure sufficient test coverage.

---

<p align="center"><em>End of prompt</em></p>

---

### Class Abstractions

Restructure the code from our main App.java class to separate the logic related to command parsing and execution logic, and the actual running of the program. The App class should only have the main method, and it should only be a maximum of 25 lines long. All other logic from the App class should be abstracted into methods and other classes.

---

<p align="center"><em>End of prompt</em></p>

---

### Regression Testing

Context:
 - Current code: App.java, CommandExecutor.java, CommandParser.java, Robot.java, SimulationController.java, AppTest.java, RobotTest.java 
 - Constraint: Testing should focus on finding any bugs produced by the recent structural changes.

Outcome:
 - Produce testing files of the form "<Class Name>Test.java" for each of the classes in the Maven build Java project.
 - Produce Result.md file that reveals gaps in the current tests, as well as remedies to these gaps.
 - Produce Metrics.md file containing code metrics for the program code.

Steps:
 1. Read the files App.java, CommandExecutor.java, CommandParser.java, Robot.java, SimulationController.java, AppTest.java, RobotTest.java in the Maven build project root.

 2. Regression Testing:
   2.1 Update AppTest.java to contain any existing or new test cases for the App class.
   2.2 Create CommandExecutorTest.java to contain any existing or new test cases for the CommandExecutor class.
   2.3 Create CommandParserTest.java to contain any existing or new test cases for the CommandParser class.
   2.4 Update RobotTest.java to contain any existing or new test cases for the Robot class.
   2.5 Create SimulationControllerTest.java to contain any existing or new test cases for the SimulationController class.

 3. Move all deprecated test cases to a file called "Deprecated.java".  Comment out all the test functions in this file.

 4. Ensure enhanced test coverage, data flow testing, and mutation testing for all the classes. 

 5. In Result.md, list findings for testing gaps related to:
   - Overall Test Coverage (Line, Statement, Decision, and Condition).
   - Data-Flow Testing (LKW Framework: All-Defs, All-Uses, All-DU Pairs, and Antidecomposition)
   - Mutation Testing (Test Case Sensitivity)
   - Remedies for testing gaps.
   - Any potential bugs uncovered through regression testing.

 6. In Metrics.md, an ordered list with the following metrics of the program code:
   - Code Complexity with Cyclomatic Complexity per Function, #lines, #functions, #classes, and #variables.
   - Minimum Test cases
   - Number of Tests Retained
   - Number of Tests Deprecated
   - Number of Tests Added
   - Which tests passed, failed, or threw exceptions

Tools:
 - JUnit5
 - Maven
 - JaCoCo
 - SonarQube
 - Pitest

Audience:
 - Developer implementing and testing the program code.
 - QA Team validating the program code.

Relevance:
 - Comprehensive Regression Testing to enhance test coverage, data flow testing, and mutation testing related to the new program structure.

Additional constraints:
 - Keep current program code intact.
 - Keep the current QATest.java file intact
 - Do not create any files other than the ones specified.
 - No deprecated test cases should be deleted.

---

<p align="center"><em>End of prompt</em></p>

---

### Improving Final Code Coverage and Test Robustness

Context

You are testing a Java-based robot simulation program that has recently been refactored from a single monolithic class (`App`) into three collaborating classes:

- **`SimulationController`** — manages the main program loop, user input, and startup initialization. It accepts an injectable `Scanner` via a secondary constructor `SimulationController(CommandParser parser, Scanner scanner)`, making the loop testable without redirecting `System.in` globally.
- **`CommandParser`** — handles all input validation logic via the public method `isValidCommand(String command)`. It tokenizes input using `.trim().split("\\s+")` (any whitespace sequence).
- **`CommandExecutor`** — holds the robot state (`Robot`, `int[][] floor`, `Queue<String> commandHistory`, `boolean isRunning`) as instance fields and exposes `executeCommand(String command, boolean addToHistory)`, `initialize(int n)`, `quit()`, `history()`, and `print()`.

The `Robot` class starts at position (0, 0), pen UP, facing NORTH. It moves on a 2D integer floor array. Movement stops silently at grid boundaries. Negative steps throw `IllegalArgumentException`.

The existing test suite (`AppTest.java`, `RobotTest.java`) tests `CommandExecutor` and `CommandParser` in isolation but **never exercises `SimulationController.run()` or `requestInitialFloorSize()` end-to-end**. Additionally, several input validation edge cases related to numeric formatting and whitespace-only inputs are not covered.

Two distinct gaps have been identified by an external QA team:

**Gap Group 1 — Untested branches in `SimulationController`:**
The initialization loop (`requestInitialFloorSize`) has four branches that are never reached by tests: valid input accepted immediately (A1), zero or negative integer rejected and looped (A2), non-integer input caught as `NumberFormatException` and looped (A3), and multiple successive invalid inputs before a valid one (A2+A3). The main command loop (`run`) has four additional untested aspects: the menu being printed once per iteration via `SHOULD_PRINT_MENU = true` (B1), history being seeded with `"I n"` immediately after initialization (B3), multiple sequential commands being processed with state persisting across iterations (B4), and the `scanner.close()` call being reached after `isRunning` becomes false (B5). Branch B2 (`SHOULD_PRINT_MENU = false`) is dead code and cannot be tested.

**Gap Group 2 — Untested input validation edge cases in `CommandParser`:**
The following inputs are not covered: integer overflow values (`"m 2147483648"`, `"i 2147483648"`), float-formatted numbers (`"m 3.5"`, `"i 4.0"`), scientific notation (`"m 1e5"`), hexadecimal notation (`"m 0x10"`), an unknown two-token command (`"x 5"`), special character commands (`"@"`, `"!"`, `"#5"`), whitespace-only variants (`"\t"`, `"\n"`), the undocumented accepted edge cases `"m -0"` (parses to 0, accepted) and `"m +5"` (parses to 5, accepted), and the cumulative robot state integrity after a sequence of consecutive invalid commands.

Outcome

Looking at the new test structure, generate the tests into the corresponding test file. For example, add tests related to CommandParser to CommandParserTest.java and so on. Just add them to the existing files. Don't modify existing tests. Add tests in the package `com.robot.COEN448_Project` that cover all of the following test cases:

**Gap Group 1 — `SimulationController` loop tests (TC-ML-01 to TC-ML-08):**

| Test ID | What to assert |
|---------|---------------|
| TC-ML-01 | Valid floor size entered on first try → no error message, program starts and quits cleanly |
| TC-ML-02 | Zero entered then valid size → error `"N must be an integer greater than 0"` printed once, program runs |
| TC-ML-03 | Negative integer entered then valid size → same error message, program runs |
| TC-ML-04 | Non-integer `"abc"` entered, then valid size → error `"Please enter a whole number"` printed once |
| TC-ML-05 | Four invalid inputs (`"0"`, `"abc"`, `"-1"`, `"3.5"`) then valid `"5"` → at least 4 error messages printed |
| TC-ML-06 | After `run()` completes with input `"5\nq\n"` → `commandHistory` contains `"I 5"` as first entry |
| TC-ML-07 | Three commands issued (`"u"`, `"d"`, `"q"`) → `"Available Commands:"` appears exactly 3 times in output |
| TC-ML-08 | Commands `"d"`, `"m 3"`, `"r"`, `"m 2"`, `"q"` issued → robot ends at (2, 3), facing EAST, pen DOWN |



**Gap Group 2 — `CommandParser` / `CommandExecutor` validation tests (TC-IV-01b to TC-IV-15):**

| Test ID | Input | Assert |
|---------|-------|--------|
| TC-IV-01b | `"m  5"` | Accepted, robot moves 5 steps north |
| TC-IV-02b | `"m\t5"` | Accepted, robot moves 5 steps north |
| TC-IV-03b | `"  m   5  "` | Accepted, robot moves 5 steps north |
| TC-IV-04 | `"m 2147483648"` | Rejected with distance error message, robot unmoved |
| TC-IV-05 | `"i 2147483648"` | Rejected with size error message, floor unchanged |
| TC-IV-06 | `"m 3.5"` | Rejected with distance error message |
| TC-IV-07 | `"i 4.0"` | Rejected with size error message, floor unchanged |
| TC-IV-08 | `"m 1e5"` | Rejected with distance error message |
| TC-IV-09 | `"m -0"` | Accepted as 0-step move, robot unmoved, floor unmarked |
| TC-IV-10 | `"m +5"` | Accepted, robot moves 5 steps north |
| TC-IV-11 | `"x 5"` | Rejected with `"Invalid Command. Please try again."` |
| TC-IV-12 | `"@"`, `"!"`, `"#5"` | Each rejected with `"Invalid Command. Please try again."` |
| TC-IV-13 | `"m 0x10"` | Rejected with distance error message |
| TC-IV-14 | `"\t"`, `"\n"` | Each rejected with `"Empty Command. Please try again."` |
| TC-IV-15 | Sequence of 6 invalid commands | Robot remains at (0,0), pen UP, facing NORTH, zero floor marks |

Steps

To generate the tests, follow this derivation process:

1. **For TC-ML-01 to TC-ML-08:** Use the injectable constructor `SimulationController(CommandParser parser, Scanner scanner)`. Build the `Scanner` from a `ByteArrayInputStream` wrapping the simulated input string. Capture `System.out` using a `ByteArrayOutputStream` during the `run()` call. After `run()` returns, inspect the captured output for expected messages and use Java reflection to read private instance fields (`commandHistory`, `robot`) from `CommandExecutor` to assert robot state and history contents.

2. **For TC-IV-01b to TC-IV-03b:** Instantiate `CommandParser` and `CommandExecutor` directly (same pattern as `AppTest.java`). Call `executor.executeCommand(input, true)` and assert robot position moved to (0, 5) to confirm the command was accepted and executed.

3. **For TC-IV-04 to TC-IV-15:** Instantiate `CommandParser` and `CommandExecutor` directly. Use the `captureStdout(Runnable)` helper to capture output. Assert the correct rejection message appears using `assertTrue(output.contains(...))`. For tests involving state integrity (TC-IV-05, TC-IV-07, TC-IV-15), also use reflection to verify floor size and robot position are unchanged.

4. For TC-IV-09 (`"m -0"`), set pen DOWN before the move to ensure the floor would be marked if any movement occurred — then assert zero marks and zero displacement to prove the 0-step behaviour.

5. For TC-IV-15, fire all 6 invalid commands in sequence without resetting the state between them, then assert the full robot state invariant.

Tools

- **Test framework:** JUnit 5 (`org.junit.jupiter.api.Test`, `@BeforeEach`, assertions from `org.junit.jupiter.api.Assertions`)
- **Input simulation:** `java.io.ByteArrayInputStream` + `java.util.Scanner` injected via `SimulationController(CommandParser, Scanner)`
- **Output capture:** `java.io.ByteArrayOutputStream` + `java.io.PrintStream` redirecting `System.setOut`
- **State inspection:** `java.lang.reflect.Field` with `setAccessible(true)` — same pattern as `AppTest.java` — to read private fields `robot`, `floor`, `commandHistory`, `isRunning` from `CommandExecutor.`
- **No mocking framework required** — the injectable constructor and reflection are sufficient

Audience

The generated tests will be added to the existing test suite (`AppTest.java`, `RobotTest.java`) in the Maven project at `src/test/java/com/robot/COEN448_Project/`. They are intended for:

- The development team, to verify that the refactored `SimulationController` behaves correctly end-to-end
- The project's CI pipeline (Maven Surefire), which runs all tests on each commit
- The external QA team, as evidence that all identified gaps have been addressed

The test class must be self-contained, use `@BeforeEach` to reset state between tests, and follow the naming conventions of `AppTest.java` (camelCase, descriptive method names).

Relevance

The gaps being addressed represent the two most critical weaknesses flagged by the external QA team:

1. **The `SimulationController` loop has never been tested as a unit.** If the initialization validation or command loop regresses — for example, after a future refactor of the Scanner injection or the history seeding logic — there is currently no test that would catch it. A regression here would mean the entire program entry point is broken with no automated signal.

2. **Input validation edge cases are not tested.** The `\\s+` tokenization fix was a silent behaviour change: inputs like `"m  5"` were previously rejected and are now accepted. Without tests locking in this new behaviour, it could be silently reverted. Similarly, overflow values, float inputs, and hex notation represent realistic user mistakes that should be documented and guarded by regression tests.

Failing to cover these gaps means the test suite provides false confidence: the program could break at startup or mishandle user input without any test failing.

---

<p align="center"><em>End of prompt</em></p>

---
