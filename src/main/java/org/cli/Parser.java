package org.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static List<Command> parse(String inputLine, Environment env) {
        List<Command> commands = new ArrayList<>();
        List<String> tokens = tokenize(inputLine);
        if (tokens.size() == 1 && setVarIfNeed(tokens.getFirst(), env)) {
            return new ArrayList<>();
        }
        List<String> singleCommand = new ArrayList<>();
        for (String token : tokens) {
            if (token.equals("|")) {
                commands.add(new Command(singleCommand));
                singleCommand.clear();
            } else {
                String replaceVars = findVarsAndReplace(token, env);
                singleCommand.add(evalQuotes(replaceVars));
            }
        }
        commands.add(new Command(singleCommand));
        return commands;
    }

    private static String findVarsAndReplace(String token, Environment env) {
        String setVarRegex = "([^'$]*('[^']*')?[^'$]*)(\\$([^$\"']+))";
        Matcher matcher = Pattern.compile(setVarRegex).matcher(token);
        return matcher.replaceAll(match ->
                match.group(1).replaceAll("\\$", "\\\\\\$") + env.getVar(match.group(4)));
    }

    private static boolean setVarIfNeed(String token, Environment env) {
        String setVarRegex = "([^=\"']+)=(.*)";
        Matcher matcher = Pattern.compile(setVarRegex).matcher(token);
        if (matcher.matches()) {
            env.setVar(matcher.group(1), evalQuotes(matcher.group(2)));
            return true;
        }
        return false;
    }

    private static String evalQuotes(String token) {
        String quoteRegex = "'((?:\\\\.|[^'])*?)'|\"((?:\\\\.|[^\"])*?)\"";
        Matcher matcher = Pattern.compile(quoteRegex).matcher(token);
        return matcher.replaceAll(match -> {
                    if (match.group(1) != null) {
                        // we are inside '...' ->
                        return match.group(1)
                                .replaceAll("\\\\", "\\\\\\\\")
                                .replaceAll("\\$", "\\\\\\$");
                    } else if (match.group(2) != null) {
                        // we are inside "..."
                        return match.group(2)
                                .replaceAll("\\\\n", "\n")
                                .replaceAll("\\\\t", "\t")
                                .replaceAll("\\$", "\\\\\\$");
                    }
                    return "";
                }
        );
    }

    private static List<String> tokenize(String inputLine) {
        List<String> tokens = new ArrayList<>();
        String tokenRegex = "\\||([^|\\s\"']*(\"((?:\\\\.|[^\"])*?)\"|'((?:\\\\.|[^'])*?)')[^|\\s\"']*)+|[^|\\s\"']+";
        Matcher matcher = Pattern.compile(tokenRegex).matcher(inputLine);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}
