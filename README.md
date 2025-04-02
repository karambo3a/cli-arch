# cli-arch

![Build Status](https://github.com/karambo3a/cli-arch/actions/workflows/ci.yaml/badge.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Simple CLI.

Supported operations:

1. Basic operations

    * ```cat [FILE]``` — print the content of the file.
    * ```echo [ARG...]``` — print the argument (or arguments).
    * ```wc [FILE]``` — print the number of lines, words and bytes in the file.
    * ```pwd``` — print the current directory.
    * ```grep [FLAGS] "PATTERN" [FILE]``` — search and print pattern lines from file
        * ```-w``` — whole word search.
        * ```-i``` — case-insensitive search.
        * ```-A  NUM``` — print NUM lines after match.
    * exit — exit the interpreter.
2. Full and weak quoting
    ```
    > echo ’What do you get if you multiply six by nine?\n Six by nine. Forty two.’
    ```
    ```
    > echo "What do you get if you multiply six by nine?\n Six by nine. Forty two."
    ```
3. Environment and variables
    ```
    > FILE=example.txt
    > cat $FILE
    ```
4. External program execution
    * If an unknown command is entered, the interpreter should attempt to execute it as an external program.
5. Pipelines
    * Support for the `|` operator to pass the output of one command as input to another

---

## Command Line Parsing

For the implementation of the `grep` command, we evaluated the following libraries
and frameworks for parsing command line parameters:

### Apache Commons CLI

**Benefits**:

1. Low learning curve with detailed documentation and numerous online examples
2. Supports both short and long flags
3. Handles positional arguments
4. Includes help message support

**Drawbacks**:

1. No annotation support, requiring more boilerplate code
2. No support for command subcommands
3. All values are returned as Strings, requiring manual parsing
4. Manual input validation

**Best for**: Small CLI applications where simplicity is prioritized

---

### JCommander

**Benefits**:

1. Annotation-based configuration
2. Supports nested/subcommands
3. Built-in validation
4. Allows default value specification
5. Well-documented with active community

**Drawbacks**:

1. No native color output support
2. Lacks command auto-completion
3. Requires Java 8 or higher

**Best for**: Medium complexity CLI applications needing more features than Apache Commons CLI

---

### Picocli

**Benefits**:

1. Comprehensive auto-completion support
2. Built-in color output
3. Includes help message support as tables
4. Annotation-based validation
5. Automatic type conversion
6. Excellent subcommand support

**Drawbacks**:

1. More complex configuration
2. Larger dependency footprint

**Best for**: Production-grade CLI applications requiring rich features

---

### Airline

**Benefits**:

1. Integrated validation system
2. Supports unlimited command nesting
3. Flexible command definition (annotations or programmatic)

**Drawbacks**:

1. No built-in auto-completion
2. No color output support
3. Overly complex for small utilities
4. Smaller community support

**Best for**: Complex CLI applications with deep command hierarchies

---

## Our decision

We selected **JCommander** because:

- It provides more features than Apache Commons CLI
- The annotation support improves development efficiency
- The additional features in Picocli and Airline would be overhead for our project

---

## Installation and Run

### Requirements

* Java Development Kit (JDK) version 11 or higher installed.
* Gradle installed for building the project.

---

### First Method

Clone the repository:

```
git clone git@github.com:karambo3a/cli-arch.git
cd cli-arch
```

Run the project using Gradle only:

```
./gradlew runCLI
```

---

### Second Method

Clone the repository:

```
git clone git@github.com:karambo3a/cli-arch.git
cd cli-arch
```

Build the project using Gradle:

```
./gradlew runCLI
```

Run the project:

```
java -jar build/libs/cli.jar
```

---

### Tests

To run the unit tests, use:

```
./gradlew test
```

---

## License

This project is licensed under the [MIT license](LICENSE)

## Contributors

* [Zalilova Diana](https://www.github.com/mediana105)
* [Ivanova Arina](https://www.github.com/Arishkamu)
* [Isaeva Ekaterina](https://www.github.com/karambo3a)

HSE SPB, AMIS-3
