package org.cli;

import java.util.*;

public class CLI {
    public static void main(String[] args) {
        // Using try-with-resources to automatically close Scanner
        try (Scanner scanner = new Scanner(System.in)) {
            // Create new environment
            Environment env = new Environment();

            // Loop to read input line by line until EOF or "exit" command
            while (scanner.hasNextLine()) {
                String inputLine = scanner.nextLine().trim(); // Read and trim the input
                List<Command> parserResult = Parser.parse(inputLine, env); // Parse input line
                int returnCode = Pipeline.pipe(parserResult); // pipeline or single command
                env.setVar("?", String.valueOf(returnCode)); // update return code
            }
        } // Scanner is automatically closed here
    }
}