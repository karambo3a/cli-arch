package org.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

// Class for processing pipelines
public class Pipeline {

    // Method to execute pipe
    public static int pipe(List<Command> commands) {
        // One command
        if (commands.size() == 1) {
            Command command = commands.getFirst();
            // Command is exit command
            if (command.isExit()) {
                System.exit(0);
            }
            return Executor.execute(command);
        }

        // Pipe with more than one command
        List<Process> processes = new ArrayList<>();
        try {
            // Start all the processes and connect their stdin/stdout
            Process prevProcess = null;
            ProcessBuilder pb = new ProcessBuilder();

            for (Command command : commands) {
                // Skip exit command, do not process it
                if (command.isExit()) {
                    continue;
                }
                pb.command(command.getName());
                pb.command().addAll(command.getArgs());

                // Redirect stdin (except first command)
                if (prevProcess != null) {
                    pb.redirectInput(ProcessBuilder.Redirect.PIPE);
                }

                // Redirect stdout (except last command)
                if (command != commands.getLast()) {
                    pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
                } else {
                    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                }

                // Redirect stderr
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                Process process = pb.start();
                processes.add(process);

                // Connect the stdout of the previous process to the stdin of the current process
                if (prevProcess != null) {
                    try (InputStream prevOut = prevProcess.getInputStream();
                         OutputStream currIn = process.getOutputStream()) {
                        prevOut.transferTo(currIn);
                    } catch (IOException e) {
                        // Skip broken pipe
                        if (!e.getMessage().contains("Broken pipe")) {
                            System.err.println(command.getName() + ": " + e.getMessage());
                        }
                    }
                }
                prevProcess = process;
            }

            int exitCode = 0;
            // Wait processes
            for (Process p : processes) {
                exitCode = p.waitFor();
            }
            // exit code of the last command in pipeline
            return exitCode;
        } catch (IOException | InterruptedException e) {
            System.err.println("Pipeline error: " + e.getMessage());
            return 1;
        }
    }
}