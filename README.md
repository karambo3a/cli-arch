# cli-arch
![Build Status](https://github.com/karambo3a/cli-arch/actions/workflows/ci.yaml/badge.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Simple command line interpreter.

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

## Build

### For Linux


### For Windows (MSVC)


### For macOS


## Contributing

## License

This project is licensed under the [MIT license](LICENSE)

## Contributors

* [Zalilova Diana](https://www.github.com/mediana105)
* [Ivanova Arina](https://www.github.com/Arishkamu)
* [Isaeva Ekaterina](https://www.github.com/karambo3a)
* [Mukhametvalieva Alina](https://www.github.com/Alina-Muha)

HSE SPB, AMIS-3
