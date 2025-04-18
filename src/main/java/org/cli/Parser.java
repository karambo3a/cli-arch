package org.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Parser provide public static function parse
public class Parser {

    /**
     * Parses the input command line string into a list of commands.
     * Supports environment variable substitution, quote handling, and pipelines (|).
     *
     * @param inputLine the raw input string from the user
     * @param env       the environment containing variable mappings
     * @return          a list of parsed Command objects
     */
    public static List<Command> parse(String inputLine, Environment env) {
        List<Command> commands = new ArrayList<>();
        if (inputLine == null || inputLine.trim().isEmpty()) {
            System.err.println("Parser error: Input cannot be empty or null.");
            return new ArrayList<>(); // Return empty list for empty input
        }

        // Matches not closed quotes
        String notClosedQuotes = "([^']*'([^']*('[^']*')*)*)|((\\\\\"|[^\"])*\"((\\\\\"|[^\"\\\\])*(\"(\\\\\"|[^\"])*\")*)*)";
        Matcher matchNotClosed = Pattern.compile(notClosedQuotes).matcher(inputLine);
        if (matchNotClosed.matches()) {
            System.err.println("Parser error: Unclosed quotes.");
            return new ArrayList<>(); // Return empty list for empty input
        }

        List<String> tokens = tokenize(inputLine);  // Tokenize the input line
        // If it's a single token, and it's a variable assignment, handle it separately

        if (tokens.size() == 1 && setVarIfNeed(tokens.getFirst(), env)) {
            return new ArrayList<>();
        }

        List<String> tokensAfterVars = new ArrayList<>();
        for (String token : tokens) {
            // Replace variables with their values
            String replaceVars = findVarsAndReplace(token, env);
            // tokenize again because variable can contain '|' or spaces
            tokensAfterVars.addAll(tokenize(replaceVars));
        }

        List<String> singleCommand = new ArrayList<>();
        // Iterate through tokens and process commands
        for (String token : tokensAfterVars) {
            if (token.equals("|")) {
                // If we encounter a pipeline "|", create a new command and add it to the list
                commands.add(new Command(singleCommand));
                singleCommand.clear();
            } else {
                // Handle quotes (escaping rules for weak and strong quotes)
                singleCommand.add(evalQuotes(token));
            }
        }

        // Add the final command (if any)
        if (!singleCommand.isEmpty()) {
            commands.add(new Command(singleCommand));
        }
        return commands;
    }

    // Replaces occurrences of environment variables in the token.
    private static String findVarsAndReplace(String token, Environment env) {
        // Matches variables like $VAR
        String setVarRegex = "('[^']*')|(\\$([^$\"']+))";
        Matcher matcher = Pattern.compile(setVarRegex).matcher(token);
        return matcher.replaceAll(match -> {
            String matchGroup3 = match.group(3);
            if (matchGroup3 != null) {
                return env.getVar(matchGroup3); // Replace with the actual variable value
            }
            return Matcher.quoteReplacement(match.group());
        });
    }

    // Checks if a token is a variable assignment (e.g., VAR=value) and sets it in the environment.
    private static boolean setVarIfNeed(String token, Environment env) {
        String setVarRegex = "([^=\"']+)=(.*)";  // Matches "VAR=value" format
        Matcher matcher = Pattern.compile(setVarRegex).matcher(token);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            String varValue = matcher.group(2);

            if (varValue.isEmpty()) {
                System.err.println("Parser error: Variable value cannot be empty.");
                return false;
            }

            // Store the variable in the environment after handling quotes
            env.setVar(varName, evalQuotes(varValue));
            return true;
        }
        return false;
    }

    /**
     * Handles quoting rules for single ('...') and double ("...") quotes.
     * - Single quotes: Treats everything literally.
     * - Double quotes: Allows escape sequences (\n, \t) but not variable substitution.
     */
    private static String evalQuotes(String token) {
        String quoteRegex = "'([^']*)'|\"((?:\\\\.|[^\"])*?)\"";
        Matcher matcher = Pattern.compile(quoteRegex).matcher(token);
        return matcher.replaceAll(match -> {
            if (match.group(1) != null) {
                // Inside '...' single quotes (strong quoting) - escape \ and $
                return Matcher.quoteReplacement(match.group(1));
            } else if (match.group(2) != null) {
                // Inside "..." double quotes (weak quoting) - process escape sequences
                return match.group(2).replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t").replaceAll("\\$", "\\\\\\$");
            }
            return "";
        });
    }

    /**
     * Tokenizes the input string into individual components.
     * - Supports command arguments, quotes, and pipeline (`|`).
     * - Keeps quoted substrings as single tokens.
     */
    private static List<String> tokenize(String inputLine) {
        List<String> tokens = new ArrayList<>();

        // Tokenization regex:
        // - Matches pipes (|) separately
        // - Matches sequences of characters that include quotes
        // - Matches other standard tokens
        String tokenRegex = "\\||([^|\\s\"']*(\"((?:\\\\.|[^\"])*?)\"|'([^']*)')[^|\\s\"']*)+|[^|\\s\"']+";
        Matcher matcher = Pattern.compile(tokenRegex).matcher(inputLine);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}
