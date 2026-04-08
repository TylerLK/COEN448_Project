# Metrics

1. Code complexity snapshot of the production code:
   `App.java` - 1 class, 1 function, 0 fields, 11 lines, cyclomatic complexity 1.
   `CommandExecutor.java` - 1 class, 8 functions, 4 fields, 162 lines, cyclomatic complexity by function: constructor 1, executeCommand 9, initialize 1, print 2, history 2, quit 1, isRunning 1, addToHistory 1.
   `CommandParser.java` - 1 class, 5 functions, 1 field, 95 lines, cyclomatic complexity by function: isValidCommand 6, isSingleTokenCommand 1, isValidNoArgumentCommand 2, isValidDistanceCommand 4, isValidInitializeCommand 4.
   `Robot.java` - 1 class, 11 functions, 4 fields, 167 lines, cyclomatic complexity by function: constructor 1, penUp 1, penDown 1, turnRight 2, turnLeft 2, move 5, isInsideFloor 1, toString 1, getX 1, getY 1, getPenOrientation 1, getDirection 1.
   `SimulationController.java` - 1 class, 5 functions, 3 fields, 78 lines, cyclomatic complexity by function: constructor 1, run 3, requestInitialFloorSize 4, printMenu 1.

2. Minimum executable test cases needed for the current regression split: 57. That is 28 new class-focused regression tests plus 29 retained tests in `QATest.java`.

3. Tests retained: 29. `QATest.java` remains intact and still contributes the original QA regression cases.

4. Tests deprecated: 35. These are the archived App and Robot regression tests preserved in commented form inside `Deprecated.java`.

5. Tests added: 28 active tests across `AppTest`, `CommandExecutorTest`, `CommandParserTest`, `RobotTest`, and `SimulationControllerTest`.

6. Test run status after the refactor split: Maven `verify` executed 57 tests and all of them passed, with no failures and no errors.

7. Quality note: the highest-risk logic remains the history replay path in `CommandExecutor`, because it depends on both queue order and replay mutation behavior. That is the primary place to target future regression, mutation, and data-flow coverage work.
