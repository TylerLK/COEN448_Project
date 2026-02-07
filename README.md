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

## VS Code Launch Configuration

There is a sample `.vscode/launch.json` with a configuration named `App` that
launches `com.robot.COEN448_Project.App`. If you run the program from the
debugger, ensure the configuration includes:

```jsonc
"console": "integratedTerminal"
```

so the program can accept interactive input.

## Project Structure

- `src/main/java` — application source code
- `src/test/java` — JUnit tests
- `pom.xml` — Maven project file

## Notes

- The `App` class validates user commands and prints user-facing error messages.
- Low-level behavior (exceptions thrown by `Robot`) is covered by unit tests in
	the test suite.
