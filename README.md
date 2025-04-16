# cli-arch
![Build Status](https://github.com/karambo3a/cli-arch/actions/workflows/ci.yaml/badge.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Simple CLI.

Supported operations:

1. Basic operations

    * cat [FILE] — print the content of the file.
    * echo — print the argument (or arguments).
    * wc [FILE] — print the number of lines, words and bytes in the file.
    * pwd — print the current directory.
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
## Installation and run
### Requirements
* Java Development Kit (JDK) version 11 or higher installed.
* Gradle installed for building the project.
---
### First method
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
### Second method
Clone the repository:
```
git clone git@github.com:karambo3a/cli-arch.git
cd cli-arch
```
Build the project using Gradle:
```
./gradlew build
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
