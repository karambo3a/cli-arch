package org.cli;

import java.io.*;
import java.util.Map;
import java.util.function.Function;

public class Executor {

    // Map of methods for builtin commands
    private static final Map<String, Function<Command, Integer>> BUILTIN_FUNCTIONS = Map.of(
            "cat", Executor::executeCat,
            "echo", Executor::executeEcho,
            "wc", Executor::executeWc,
            "pwd", Executor::executePwd
    );


    // Method to execute builtin and external commands
    public static int execute(Command command) {
        if (command.isBuiltin()) {
            return executeBuiltin(command);
        }
        return executeExternal(command);
    }


    // Method to execute the external command
    public static int executeExternal(Command command) {
        ProcessBuilder pb = new ProcessBuilder(command.getName());
        pb.command().addAll(command.getArgs());
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();

            // Redirect stdin to the process (if not System.in)
            if (command.getStdin() != System.in) {
                try (OutputStream processInput = process.getOutputStream()) {
                    command.getStdin().transferTo(processInput);
                }
            }

            // Redirect process stdout and command.getStdout()
            try (InputStream processOutput = process.getInputStream()) {
                processOutput.transferTo(command.getStdout());
            }
            // Wait process
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            if (!e.getMessage().contains("Broken pipe")) {
                System.err.println(command.getName() + ": " + e.getMessage());
                return 1;
            }
            return 0;
        }
    }


    // Method to execute the builtin command
    private static int executeBuiltin(Command command) {
        return BUILTIN_FUNCTIONS.containsKey(command.getName()) ?
                BUILTIN_FUNCTIONS.get(command.getName()).apply(command) :
                unknownBuiltinCommand(command);
    }


    // Method to execute the `cat` command
    private static int executeCat(Command command) {
        OutputStream output = command.getStdout();
        InputStream input = null;
        int exitCode = 0;
        try {
            // Read data from input stream and write to output stream
            input = getInputStream(command);
            input.transferTo(output);
        } catch (IOException e) {
            System.err.println("cat: " + e.getMessage());
            exitCode = 1;
        } finally {
            if (input != null && input != System.in) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("cat: " + e.getMessage());
                    exitCode = 1;
                }
            }
        }
        return exitCode;
    }


    // Method to execute the `echo` command
    private static int executeEcho(Command command) {
        OutputStream output = command.getStdout();
        int exitCode = 0;
        try {
            // Join all arguments in a single line with spaces
            String result = String.join(" ", command.getArgs()) + "\n";
            // Write the result to the output
            output.write(result.getBytes());
            // Flush the output stream to ensure data is written
            output.flush();
        } catch (IOException e) {
            System.err.println("echo: " + e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }


    // Set input for wc and cat: stdin or file
    private static InputStream getInputStream(Command command) throws IOException {
        if (command.getArgs().isEmpty()) {
            return command.getStdin();
        }
        String file = command.getArgs().getFirst();
        return new FileInputStream(file);
    }


    // Method to execute the `wc` command
    private static int executeWc(Command command) {
        OutputStream output = command.getStdout();
        String fileName = command.getArgs().isEmpty() ? "" : command.getArgs().getFirst();
        InputStream input = null;
        int exitCode = 0;
        long lineCnt = 0, wordCnt = 0, byteCnt = 0;
        try {
            input = getInputStream(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                lineCnt++;
                wordCnt += line.isBlank() ? 0 : line.trim().split("\\s+").length;
                byteCnt += line.getBytes().length + 1;
            }
            // Join all stat in a single line with spaces
            String result = String.format("%7d %7d %7d %s%n", lineCnt, wordCnt, byteCnt, fileName);
            // Write the result to the output
            output.write(result.getBytes());
            // Flush the output stream to ensure data is written
            output.flush();
        } catch (IOException e) {
            System.err.println("wc: " + e.getMessage());
            exitCode = 1;
        } finally {
            if (input != null && input != System.in) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("wc: " + e.getMessage());
                    exitCode = 1;
                }
            }
        }
        return exitCode;
    }


    // Method to execute the `pwd` command
    private static int executePwd(Command command) {
        OutputStream output = command.getStdout();
        int exitCode = 0;
        try {
            // Get the current working directory
            String currentDirectory = System.getProperty("user.dir") + "\n";
            // Write the directory path to the output stream
            output.write(currentDirectory.getBytes());
            // Flush the output stream to ensure data is written
            output.flush();
        } catch (IOException e) {
            exitCode = 1;
        }
        return exitCode;
    }


    // Process unknown builtin command
    private static int unknownBuiltinCommand(Command command) {
        System.err.println(command.getName() + ": unknown command");
        return 1;
    }
}
