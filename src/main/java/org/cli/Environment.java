package org.cli;

import java.util.HashMap;
import java.util.Map;

// Environment class manages environment variables
public class Environment {

    // Map to store environment variables
    private final Map<String, String> vars;

    // Constructor initializes the environment and sets "?" to "0"
    public Environment() {
        this.vars = new HashMap<>();
        this.vars.put("?", "0"); // Default return code is 0 (success)
    }

    // Get the value of an environment variable or empty string if key is not found
    public String getVar(String key) {
        System.out.println("getVars.key=" + key);
        return vars.getOrDefault(key, "");
    }

    // Set or update an environment variable
    public void setVar(String key, String value) {
        System.out.println("setVars.key=" + key + " value=" + value);
        vars.put(key, value);
    }

    // Check if an environment variable exists
    public boolean containsVar(String key) {
        return vars.containsKey(key);
    }
}