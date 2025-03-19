package org.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class ParserTest {

    private static final Environment env = new Environment();
    private static Method findVarsAndReplace;
    private static Method setVarIfNeed;
    private static Method evalQuotes;
    private static Method tokenize;


    @BeforeAll
    public static void setUp() throws NoSuchMethodException {
        findVarsAndReplace = Parser.class.getDeclaredMethod("findVarsAndReplace", String.class, Environment.class);
        findVarsAndReplace.setAccessible(true);

        setVarIfNeed = Parser.class.getDeclaredMethod("setVarIfNeed", String.class, Environment.class);
        setVarIfNeed.setAccessible(true);

        evalQuotes = Parser.class.getDeclaredMethod("evalQuotes", String.class);
        evalQuotes.setAccessible(true);

        tokenize = Parser.class.getDeclaredMethod("tokenize", String.class);
        tokenize.setAccessible(true);
    }

    @BeforeEach
    public void beforeEach() {
        env.setVar("USER_BOB", "Bob");
        env.setVar("HOME_BOB", "/home/bob");
    }

    @Test
    public void testParse() {

    }

    @Test
    void testTokenize_SimpleCommand() throws InvocationTargetException, IllegalAccessException {
        String input = "echo hello world";
        List<String> expected = List.of("echo", "hello", "world");
        List<String> tokens = (List<String>) tokenize.invoke(null, input);

        assertEquals(expected, tokens);
    }

    @Test
    void testTokenize_WithPipes() throws InvocationTargetException, IllegalAccessException {
        String input = "ls -l | grep java | wc -l";
        List<String> expected = List.of("ls", "-l", "|", "grep", "java", "|", "wc", "-l");
        List<String> tokens = (List<String>) tokenize.invoke(null, input);

        assertEquals(expected, tokens);
    }

    @Test
    void testTokenize_WithQuotes() throws InvocationTargetException, IllegalAccessException {
        String input = "echo \"hello world\" 'single quoted'";
        List<String> expected = List.of("echo", "\"hello world\"", "'single quoted'");
        List<String> tokens = (List<String>) tokenize.invoke(null, input);

        assertEquals(expected, tokens);
    }

    @Test
    void testTokenize_WithEscapedQuotes() throws InvocationTargetException, IllegalAccessException {
        String input = "echo \"This is \\\"quoted\\\" text\"";
        List<String> expected = List.of("echo", "\"This is \\\"quoted\\\" text\"");
        List<String> tokens = (List<String>) tokenize.invoke(null, input);

        assertEquals(expected, tokens);
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({
            "USER, Alice, Alice",
            "HOME, /home/Alice, /home/Alice",
            "withQuotes, aaa\"bbb\"ccc, aaabbbccc",
            "x, ec, ec",
            "y, \"ho 123\", ho 123"
    })
    void testSetVarIfNeed_ValidAssignment(String key, String value, String expect) throws InvocationTargetException, IllegalAccessException {
        String input = key + "=" + value;
        boolean result1 = (boolean) setVarIfNeed.invoke(null, input, env);

        assertTrue(result1);
        assertEquals(expect, env.getVar(key));
    }

    @Test
    void testSetVarIfNeed_equalInsideQuotes() throws InvocationTargetException, IllegalAccessException {
        String input = "thisEqual\"keq=value\"insideQuotes";
        boolean result1 = (boolean) setVarIfNeed.invoke(null, input, env);

        assertFalse(result1);
    }

    @Test
    void testSetVarIfNeed_InvalidAssignment() throws InvocationTargetException, IllegalAccessException {
        String input = "echo something";
        boolean result1 = (boolean) setVarIfNeed.invoke(null, input, env);

        assertFalse(result1);
    }

    @ParameterizedTest
    @CsvSource({
            "$USER_BOB, Bob",
            "someLetters$HOME_BOB, someLetters/home/bob",
            "more'quotes'for$USER_BOB\"endBнQuote\"eee, more'quotes'forBob\"endBнQuote\"eee"
    })
    void testFindVarsAndReplace_WithVariables(String token, String expect) throws InvocationTargetException, IllegalAccessException {
        String result = (String) findVarsAndReplace.invoke(null, token, env);

        assertEquals(expect, result);
    }

    @Test
    void testFindVarsAndReplace_UnknownVariable() throws InvocationTargetException, IllegalAccessException {
        String input = "$FOO";
        String result = (String) findVarsAndReplace.invoke(null, input, env);

        assertEquals("", result); // If variable is not set, it returns "null"
    }
}
