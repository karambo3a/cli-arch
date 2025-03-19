package org.cli;

import java.io.*;
import java.util.List;

public class Executor {
    public static int execute(Command command) {
        try {
            if (command.isBuiltin()) {
                return executeBuiltin(command);
            }
            return executeExternal(command);
        } catch (IOException e) {

        }
        return 0;
    }

    private static int executeExternal(Command command) throws IOException {
        InputStream input = command.getStdin();
        OutputStream output = command.getStdout();
        ProcessBuilder processBuilder = new ProcessBuilder(command.getName());
        processBuilder.redirectErrorStream(true);
        processBuilder.command().addAll(command.getArgs());

        Process process = processBuilder.start();

        // Transfer data from stdin to the process
        try (OutputStream processInput = process.getOutputStream()) {
            if (input != System.in) {
                input.transferTo(processInput);
            }
        }

        // Transfer data from process stdout to the output of the command
        try (InputStream processOutput = process.getInputStream()) {
            processOutput.transferTo(output);
        }

        // Wait for the process to complete and get the return code
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            exitCode = 1; // Return the error code if the process was interrupted
        }

        return exitCode; // Return the process exit code
    }

    private static int executeBuiltin(Command command) {
        switch (command.getName()) {
            case "cat" -> {
                return executeCat(command);
            }
            case "echo" -> {
                return executeEcho(command);
            }
            case "wc" -> {
                return executeWc(command);
            }
            case "pwd" -> {
                return executePwd(command);
            }
        }
        return 0;
    }

    // Method to execute the `cat` command
    private static int executeCat(Command command) {
        OutputStream output = command.getStdout();
        String file = command.getArgs().getFirst();
        int exitCode = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Write the line to the output stream
                output.write(line.getBytes());
                output.write("\n".getBytes());
            }
            // Flush the output stream to ensure data is written
            output.flush();
        } catch (IOException e) {
            exitCode = 1;
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
            exitCode = 1;
        }
        return exitCode;
    }

    // Method to execute the `wc` command
    private static int executeWc(Command command) {
        OutputStream output = command.getStdout();
        String file = command.getArgs().getFirst();
        int exitCode = 0;
        long lineCnt = 0;
        long wordCnt = 0;
        long byteCnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCnt++;
                wordCnt += line.split("\\s+").length;
                byteCnt += line.getBytes().length + 1;
            }
            // Join all stat in a single line with spaces
            String result = String.join(" ",
                    List.of(
                            String.valueOf(lineCnt),
                            String.valueOf(wordCnt),
                            String.valueOf(byteCnt),
                            file
                    )) + "\n";
            // Write the result to the output
            output.write(result.getBytes());
            // Flush the output stream to ensure data is written
            output.flush();
        } catch (IOException e) {
            exitCode = 1;
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
}
