package org.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// Command class represents a command in the CLI
public class Command {
    // The name of the command (e.g., "echo", "pwd", "wc", etc.)
    private final String name;
    // List of arguments for the command
    private final List<String> args;

    // Standard input and output (can be redirected in a pipeline)
    private InputStream stdin;
    private OutputStream stdout;

    // Map defining the expected number of arguments for each built-in command
    private static final Map<String, Integer> BUILTIN_COMMANDS_AND_ARG_COUNTS = Map.of("cat", 1, "echo", -1, // -1 means any number of arguments
            "pwd", 0, "wc", 1, "exit", 0);

    public Command(List<String> tokens) {
        if (tokens.isEmpty()) {
            System.err.println("Command error: Empty command");
            this.args = Collections.emptyList();
            this.name = "";
        } else {
            this.args = List.copyOf(tokens.subList(1, tokens.size()));

            this.name = tokens.getFirst();
            if (!checkNumberOfArguments()) {
                System.err.println("Error: Invalid number of arguments for command: " + name);
            }
        }
        this.stdin = System.in;
        this.stdout = System.out;
    }

    // Setter for stdin and stdout (used in pipelines)
    public void setStdin(InputStream stdin) {
        this.stdin = stdin;
    }

    public void setStdout(OutputStream stdout) {
        this.stdout = stdout;
    }

    // Getter for stdin and stdout (used in Executor)
    public InputStream getStdin() {
        return this.stdin;
    }

    public OutputStream getStdout() {
        return this.stdout;
    }

    // Getter for command name (used in Executor)
    public String getName() {
        return this.name;
    }

    // Getter for command arguments (used in Executor)
    public List<String> getArgs() {
        return this.args;
    }

    // Method to check if the command is "exit"
    public boolean isExit() {
        return "exit".equals(name);
    }

    // Method to check if this is a built-in command
    public boolean isBuiltin() {
        return BUILTIN_COMMANDS_AND_ARG_COUNTS.containsKey(name);
    }

    // Method to validate the number of arguments for built-in commands
    private boolean checkNumberOfArguments() {
        if (!isBuiltin()) {
            return true; // External commands are not checked
        }

        int expectedArgs = BUILTIN_COMMANDS_AND_ARG_COUNTS.get(name);
        // If expectedArgs == -1, any number of arguments is allowed
        // If it is pipeline number of arguments can be one less
        return expectedArgs == -1 || expectedArgs == args.size() || expectedArgs - 1 == args.size();
    }

    @Override
    public String toString() {
        return "Command{" + "name='" + name + '\'' + ", args=" + args + ", stdin=" + stdin + ", stdout=" + stdout + '}';
    }
}
