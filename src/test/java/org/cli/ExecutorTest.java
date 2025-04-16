package org.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test class for Executor
class ExecutorTest {

    @Test
        // Test for cat command
    void testExecuteCat() throws IOException {
        String string = "Hello from file!!!\n";
        File file = File.createTempFile("test", ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(string);
        }
        Command command = new Command(List.of("cat", file.getAbsolutePath()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        command.setStdout(output);

        int exitCode = Executor.execute(command);

        assertEquals(0, exitCode);
        assertEquals(string, output.toString());
    }

    @Test
        // Test for echo command
    void testExecuteEcho() {
        String echoArg = "Hello from echo!!!\n";
        Command command = new Command(List.of("echo", echoArg));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        command.setStdout(output);

        int exitCode = Executor.execute(command);

        assertEquals(0, exitCode);
        assertEquals(echoArg + "\n", output.toString());
    }

    @Test
        // Test for pwd command
    void testExecutePwd() {
        Command command = new Command(List.of("pwd"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        command.setStdout(output);

        int exitCode = Executor.execute(command);

        assertEquals(0, exitCode);
        assertEquals(System.getProperty("user.dir") + "\n", output.toString());
    }

    @Test
        // Test for wc command
    void testExecuteWc() throws IOException {
        String string = "Hello from file!!!\nHello from file\tagain!!!\n";
        File file = File.createTempFile("test", ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(string);
        }
        Command command = new Command(List.of("wc", file.getAbsolutePath()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        command.setStdout(output);

        int exitCode = Executor.execute(command);

        assertEquals(0, exitCode);
        String expected = "2 7 44 " + file.getAbsolutePath() + "\n";
        assertEquals(expected, output.toString());
    }

    @Test
        // Test for external command
    void testExecuteExternal() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        String string = "Hello from file!!!\nHello from file\tagain!!!\n";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(string);
        }

        Command command = new Command(List.of("grep", "again", tempFile.getAbsolutePath()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        command.setStdout(output);

        int exitCode = Executor.execute(command);

        assertEquals(0, exitCode);

        String outputString = output.toString().trim();
        assertEquals("Hello from file\tagain!!!", outputString);

    }
}