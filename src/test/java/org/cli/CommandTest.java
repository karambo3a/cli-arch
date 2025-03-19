package org.cli;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    void testValidCommandWithArguments() {
        Command command = new Command(List.of("echo", "Hello", "World"));
        assertEquals("echo", command.getName());
        assertEquals(List.of("Hello", "World"), command.getArgs());
    }

    @Test
    void testValidCommandWithNoArguments() {
        Command command = new Command(List.of("pwd"));
        assertEquals("pwd", command.getName());
        assertTrue(command.getArgs().isEmpty());
    }

    @Test
    void testEmptyCommand() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Command(List.of()));
        assertEquals("Empty command", exception.getMessage());
    }

    @Test
    void testInvalidNumberOfArgumentsForCat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Command(List.of("cat", "file1", "file2")));
        assertEquals("Invalid number of arguments", exception.getMessage());
    }

    @Test
    void testValidNumberOfArgumentsForCat() {
        Command command = new Command(List.of("cat", "file1"));
        assertEquals("cat", command.getName());
        assertEquals(List.of("file1"), command.getArgs());
    }

    @Test
    void testValidNumberOfArgumentsForEcho() {
        Command command = new Command(List.of("echo", "Hello", "World"));
        assertEquals("echo", command.getName());
        assertEquals(List.of("Hello", "World"), command.getArgs());
    }

    @Test
    void testInvalidNumberOfArgumentsForEcho() {
        Command command = new Command(List.of("echo"));  // Valid as echo accepts any number of args
        assertEquals("echo", command.getName());
        assertTrue(command.getArgs().isEmpty());
    }

    @Test
    void testIsBuiltinCommand() {
        Command catCommand = new Command(List.of("cat", "file1"));
        assertTrue(catCommand.isBuiltin());

        Command externalCommand = new Command(List.of("ls", "-l"));
        assertFalse(externalCommand.isBuiltin());
    }

    @Test
    void testDefaultStdInAndOut() {
        Command command = new Command(List.of("echo", "Test"));

        // Verify default stdin and stdout
        assertEquals(System.in, command.getStdin());
        assertEquals(System.out, command.getStdout());
    }

    @Test
    void testSetCustomStdinAndStdout() {
        Command command = new Command(List.of("echo", "Test"));
        InputStream customInput = new ByteArrayInputStream("input".getBytes());
        OutputStream customOutput = new ByteArrayOutputStream();

        command.setStdin(customInput);
        command.setStdout(customOutput);

        // Verify that stdin and stdout were set correctly
        assertEquals(customInput, command.getStdin());
        assertEquals(customOutput, command.getStdout());
    }

    @Test
    void testIsExit() {
        Command commandNotExit = new Command(List.of("echo", "Test"));
        assertFalse(commandNotExit.isExit());

        Command commandExit = new Command(List.of("exit"));
        assertTrue(commandExit.isExit());
    }
}