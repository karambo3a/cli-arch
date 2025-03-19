package org.cli;

import java.util.List;

// Class for processing pipelines
public class Pipeline {
    public static int pipe(List<Command> parserResult) {
        // The case of a single command
        if (parserResult.size() == 1) {
            Command command = parserResult.getFirst();
            // Check if the exit command
            if (command.isExit()) {
                // End the program
                System.exit(0);
            }
            // Execute the command without pipes
            return Executor.execute(command);
        }
        return 0;
    }

}
