package org.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentTest {

    private Environment environment;

    @BeforeEach
    void setUp() {
        environment = new Environment();
    }

    @Test
    void testInitialization() {
        assertNotNull(environment);
        assertEquals("0", environment.getVar("?")); // Default value for "?"
    }

    @Test
    void testSetVar() {
        environment.setVar("USER", "testUser");
        assertEquals("testUser", environment.getVar("USER"));
    }

    @Test
    void testUpdateVar() {
        environment.setVar("USER", "testUser");
        environment.setVar("USER", "newUser");
        assertEquals("newUser", environment.getVar("USER"));
    }

    @Test
    void testGetNonExistentVar() {
        assertEquals("", environment.getVar("NON_EXISTENT_VAR")); // Default should be empty string
    }

    @Test
    void testContainsVarTrue() {
        environment.setVar("USER", "testUser");
        assertTrue(environment.containsVar("USER"));
    }

    // Test if the variable does not exist
    @Test
    void testContainsVarFalse() {
        assertFalse(environment.containsVar("NON_EXISTENT_VAR"));
    }

    @Test
    void testMultipleVars() {
        environment.setVar("USER", "testUser");
        environment.setVar("HOME", "/home/testUser");
        environment.setVar("PATH", "/usr/bin");

        assertEquals("testUser", environment.getVar("USER"));
        assertEquals("/home/testUser", environment.getVar("HOME"));
        assertEquals("/usr/bin", environment.getVar("PATH"));
    }
}