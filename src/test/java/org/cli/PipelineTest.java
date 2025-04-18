package org.cli;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PipelineTest {
    @Test
    public void testSingleCommand() {
        Command echo = new Command(List.of("echo", "Hello, world!"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        echo.setStdout(output);
        List<Command> commands = List.of(echo);
        int exitCode = Pipeline.pipe(commands);
        assertEquals(0, exitCode, "Exit code should be 0");
        assertEquals("Hello, world!\n", output.toString(), "Output should match expected value");
    }

    @Disabled
    @Test
    public void testPipelineWithMultipleCommands() {
        Command echoCommand = new Command(List.of("echo", "Hello, world!"));
        Command wcCommand = new Command(List.of("wc", "-l"));
        List<Command> commands = List.of(echoCommand, wcCommand);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wcCommand.setStdout(outputStream);

        int exitCode = Pipeline.pipe(commands);
        assertEquals(0, exitCode);

        String result = outputStream.toString().strip();
        assertEquals("1", result);
    }

    @Test
    public void testPipelineWithInvalidCommand() {
        Command invalidCommand = new Command(List.of("invalid_command"));
        List<Command> commands = List.of(invalidCommand);
        int exitCode = Pipeline.pipe(commands);
        assertNotEquals(0, exitCode);
    }

    @Disabled
    @Test
    public void testPipelineWithCatArgument() throws IOException {
        Path testFile = Files.createTempFile("test", ".txt");
        Files.write(testFile, List.of("File content"));

        Command echo = new Command(List.of("echo", "Hello world"));
        Command cat = new Command(List.of("cat", testFile.toString()));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        cat.setStdout(output);

        int exitCode = Pipeline.pipe(List.of(echo, cat));

        assertEquals(0, exitCode);
        String result = output.toString();
        assertTrue(result.contains("File content"));
        assertTrue(result.contains("Hello from world"));

        Files.delete(testFile);
    }

    @Test
    public void testPipelineWithErrorInSecondCommand() {
        Command echoCommand = new Command(List.of("echo", "Hello, world!"));
        Command invalidCommand = new Command(List.of("invalid_command"));

        List<Command> commands = List.of(echoCommand, invalidCommand);
        int exitCode = Pipeline.pipe(commands);

        assertNotEquals(0, exitCode);
    }

    @Test
    public void testSingleCommandWithError() {
        Command invalidCommand = new Command(List.of("invalid_command"));

        List<Command> commands = List.of(invalidCommand);
        int exitCode = Pipeline.pipe(commands);

        assertNotEquals(0, exitCode);
    }

    @Test
    public void testEmptyPipeline() {
        List<Command> commands = List.of();
        int exitCode = Pipeline.pipe(commands);
        assertEquals(0, exitCode);
    }

    @Disabled
    @Test
    public void testLargeDataPipeline() {
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeInput.append("line ").append(i).append("\n");
        }

        Command echoCommand = new Command(List.of("echo", largeInput.toString()));
        Command wcCommand = new Command(List.of("wc", "-l"));

        List<Command> commands = List.of(echoCommand, wcCommand);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wcCommand.setStdout(outputStream);

        int exitCode = Pipeline.pipe(commands);
        assertEquals(1, exitCode);

        String result = outputStream.toString().strip();
        assertEquals("10000", result);
    }


    @Test
    public void testPipelineWithIncorrectArguments() {
        Command echoCommand = new Command(List.of("echo", "Hello"));
        Command wcCommand = new Command(List.of("wc", "-z"));

        List<Command> commands = List.of(echoCommand, wcCommand);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wcCommand.setStdout(outputStream);

        int exitCode = Pipeline.pipe(commands);
        assertNotEquals(0, exitCode);
    }

    @Disabled
    @Test
    public void testPipeDataTransferBetweenEchoAndCat() {
        Command echoCommand = new Command(List.of("echo", "Hello, world!"));
        Command catCommand = new Command(List.of("cat"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        catCommand.setStdout(outputStream);

        List<Command> commands = List.of(echoCommand, catCommand);
        int exitCode = Pipeline.pipe(commands);

        assertEquals(0, exitCode);
        assertEquals("Hello, world!", outputStream.toString().strip());
    }

}
