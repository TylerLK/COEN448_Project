# Gap filling Report — COEN448 Robot Project

**Scope:** Issues 1 & 2 from QA Team Feedback — analysed against the current architecture (QATest.java excluded from scope)

---

## System Architecture

The project is structured as 5 collaborating classes:

| Class | Responsibility |
|-------|---------------|
| `App` | Entry point — instantiates `SimulationController` and calls `run()` |
| `Robot` | holds robot status, The orientation of the robot's pen and the direction the robot is facing |
| `SimulationController` | Main program loop, floor-size prompt, menu display. Accepts an injectable `Scanner` via `SimulationController(CommandParser, Scanner)` |
| `CommandParser` | Validates all user input via `isValidCommand(String)`. Tokenizes with `.trim().split("\\s+")` |
| `CommandExecutor` | Holds robot state (`Robot`, `int[][] floor`, `Queue<String> commandHistory`, `boolean isRunning`). Executes validated commands |

Input validation uses `\\s+` tokenization throughout, meaning any whitespace sequence (spaces, tabs) is treated as a delimiter.

---

## Issue 1 — Untested Branches in the Main Loop

`App.main()` is trivial (3 lines, no branches). All branch coverage concerns apply to `SimulationController.run()` and its private helper `requestInitialFloorSize()`.

The injectable constructor `SimulationController(CommandParser, Scanner)` allows the full loop to be exercised in isolation by injecting a `Scanner` backed by a `ByteArrayInputStream`, without redirecting `System.in` globally.

### Section A — Initialization Input Loop (`requestInitialFloorSize()`)

| Branch | Condition |
|--------|-----------|
| A1 | Valid positive integer entered — returns immediately |
| A2 | Zero or negative integer entered — error printed, loop repeats |
| A3 | Non-integer input — `NumberFormatException` caught, error printed, loop repeats |
| A4 | Multiple invalid inputs before a valid one (loop iterates > 1 time) |

### Section B — Main Command Loop (`run()`)

| Branch | Condition |
|--------|-----------|
| B1 | `SHOULD_PRINT_MENU = true` — `printMenu()` called each iteration |
| B2 | `SHOULD_PRINT_MENU = false` — `printMenu()` skipped — **dead code**, `SHOULD_PRINT_MENU` is `private static final boolean = true` and cannot be exercised |
| B3 | `executor.addToHistory("I " + floorSize)` seeds history after initialization |
| B4 | Multiple sequential commands processed with state persisting across iterations |
| B5 | `scanner.close()` called after `isRunning` becomes false |

### Test Cases — Issue 1

All tests reside in `SimulationControllerTest.java` and use the injectable constructor.

| Test ID | Method | Simulated Input | Assertion | Branch |
|---------|--------|-----------------|-----------|:------:|
| TC-ML-01 | `tcMl01ValidFloorSizeOnFirstTry` | `"5\nq\n"` | No error message in output; program exits cleanly | A1 |
| TC-ML-02 | `tcMl02ZeroThenValidFloorSize` | `"0\n5\nq\n"` | `"N must be an integer greater than 0"` appears exactly once | A2 |
| TC-ML-03 | `tcMl03NegativeIntegerThenValidFloorSize` | `"-3\n5\nq\n"` | Same error as TC-ML-02 appears exactly once | A2 |
| TC-ML-04 | `tcMl04NonIntegerThenValidFloorSize` | `"abc\n5\nq\n"` | `"Please enter a whole number"` appears exactly once | A3 |
| TC-ML-05 | `tcMl05MultipleInvalidsThenValidFloorSize` | `"0\nabc\n-1\n3.5\n5\nq\n"` | At least 4 error messages total across both error types | A2 + A3 |
| TC-ML-06 | `tcMl06HistorySeededWithInitCommand` | `"5\nq\n"` | `commandHistory.peek()` equals `"I 5"` after `run()` returns | B3 |
| TC-ML-07 | `tcMl07MenuPrintedOncePerIteration` | `"5\nu\nd\nq\n"` | `"Available Commands:"` appears exactly 3 times | B1 |
| TC-ML-08 | `tcMl08StatePersistsAcrossCommands` | `"5\nd\nm 3\nr\nm 2\nq\n"` | Robot at (2, 3), facing EAST, pen DOWN | B4 |

---

## Issue 2 — Input Validation Edge Cases

`CommandParser.isValidCommand()` uses `Integer.parseInt` for numeric arguments. This means inputs that look numeric but are not valid Java integers (floats, scientific notation, hex, overflow values) throw `NumberFormatException` and are rejected. Inputs that Java's `parseInt` does accept — such as `"-0"` (parses to 0) and `"+5"` (parses to 5) — are accepted by the validator.

### Whitespace Acceptance (Confirmed Correct Behaviour)

The `\\s+` tokenizer collapses any whitespace sequence into a single split. The following inputs are **accepted** and must be tested to lock in this behaviour against future regression.

| Test ID | Method | Input | Assertion | Location |
|---------|--------|-------|-----------|----------|
| TC-IV-01b | `tcIv01bDoubleSpaceAccepted` | `"m  5"` | Robot moves to (0, 5) | `CommandExecutorTest` |
| TC-IV-02b | `tcIv02bTabSeparatorAccepted` | `"m\t5"` | Robot moves to (0, 5) | `CommandExecutorTest` |
| TC-IV-03b | `tcIv03bLeadingTrailingWhitespaceAccepted` | `"  m   5  "` | Robot moves to (0, 5) | `CommandExecutorTest` |

### Rejection Cases

The following inputs must be rejected with the specified error messages. Validation is tested directly via `CommandParser` where only the parser output matters; cases that also verify executor state are tested via `CommandExecutor`.

| Test ID | Method | Input | Expected Rejection Message | Location |
|---------|--------|-------|---------------------------|----------|
| TC-IV-04 | `tcIv04IntegerOverflowInMoveRejected` | `"m 2147483648"` | `"The distance must be a non-negative integer."` | `CommandParserTest` |
| TC-IV-05 | `tcIv05IntegerOverflowInInitRejected` | `"i 2147483648"` | `"The size must be a positive integer."` | `CommandParserTest` |
| TC-IV-06 | `tcIv06FloatMoveRejected` | `"m 3.5"` | `"The distance must be a non-negative integer."` | `CommandParserTest` |
| TC-IV-07 | `tcIv07FloatInitRejected` | `"i 4.0"` | `"The size must be a positive integer."` | `CommandParserTest` |
| TC-IV-08 | `tcIv08ScientificNotationMoveRejected` | `"m 1e5"` | `"The distance must be a non-negative integer."` | `CommandParserTest` |
| TC-IV-11 | `tcIv11UnknownTwoTokenCommandRejected` | `"x 5"` | `"Invalid Command. Please try again."` | `CommandParserTest` |
| TC-IV-12 | `tcIv12SpecialCharacterCommandsRejected` | `"@"`, `"!"`, `"#5"` | `"Invalid Command. Please try again."` for each | `CommandParserTest` |
| TC-IV-13 | `tcIv13HexNotationMoveRejected` | `"m 0x10"` | `"The distance must be a non-negative integer."` | `CommandParserTest` |
| TC-IV-14 | `tcIv14WhitespaceOnlyInputsRejected` | `"\t"`, `"\n"` | `"Empty Command. Please try again."` for each | `CommandParserTest` |

### Accepted Edge Cases (Documented Behaviour)

| Test ID | Method | Input | Rationale | Assertion | Location |
|---------|--------|-------|-----------|-----------|----------|
| TC-IV-09 | `tcIv09NegativeZeroTreatedAsZeroStep` | `"m -0"` | `Integer.parseInt("-0")` returns 0; passes `s >= 0`. Pen set DOWN first to prove no marks are made | Robot stays at (0, 0), zero floor marks | `CommandExecutorTest` |
| TC-IV-10 | `tcIv10PlusPrefixAccepted` | `"m +5"` | `Integer.parseInt("+5")` returns 5; valid non-negative distance | Robot moves to (0, 5) | `CommandExecutorTest` |

### State Integrity

| Test ID | Method | Scenario | Assertion | Location |
|---------|--------|----------|-----------|----------|
| TC-IV-15 | `tcIv15InvalidCommandSequencePreservesRobotState` | Six consecutive invalid commands: `"m 3.5"`, `"i 4.0"`, `"m 2147483648"`, `"@"`, `"\t"`, `"x 5"` | Robot at (0, 0), pen UP, facing NORTH, zero floor marks | `CommandExecutorTest` |

---

## JaCoCo Coverage Gaps Addressed

After running the JaCoCo report on the test suite produced above, six additional uncovered code paths were identified and covered with targeted tests.

| Test Method | File | Code Path Covered | Why It Was Uncovered |
|-------------|------|-------------------|----------------------|
| `rejectsMoveCommandWithMissingArgument` | `CommandParserTest` | `isValidDistanceCommand` — `length != 2` true-branch (`"m"` alone, length = 1) | Existing tests only passed `"m x"` or `"m 1 extra"`, never `"m"` with no argument |
| `rejectsInitCommandWithMissingArgument` | `CommandParserTest` | `isValidInitializeCommand` — `length != 2` true-branch (`"i"` alone, length = 1) | Same gap as above for the `i` command |
| `moveCommandCatchesIllegalArgumentExceptionFromRobot` | `CommandExecutorTest` | `catch (IllegalArgumentException e)` in the `"m"` switch case | Defensive catch: `isValidCommand` normally prevents negatives from reaching `Robot.move()`. Covered by injecting an anonymous `Robot` subclass that always throws |
| `switchDefaultCasePrintsInvalidCommandMessage` | `CommandExecutorTest` | `default` case in `executeCommand` switch | Unreachable through `isValidCommand`. Covered by injecting an anonymous `CommandParser` that always returns `true`, then sending an unknown token | 
| `appDefaultConstructorIsInstantiable` | `AppTest` | `App` implicit no-arg constructor | `App.main()` is static — the class itself was never instantiated in any test |
| `executeCommandTurnLeftUpdatesDirection` | `CommandExecutorTest` | `case "l"` — `robot.turnLeft()` | No test in `CommandExecutorTest` exercised the `"l"` command through `executeCommand`; turn-left was only tested directly on `Robot` |
