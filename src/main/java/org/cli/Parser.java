package org.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static List<Command> parse(String inputLine, Environment env) {
//        	1. Разбивает строку на токены, с учетом кавычек (объединяет в один токен)
//	2. Определить если это подстановка переменной. Установить env.setVar
//       * Проверить соответствует ли токен паттерну ```string=string``` без кавычек
//	3. Если команда, выполнить подстановку окружения
//       * Пройтись по всем токенам слева направо, найти которые соответствуют паттерну ```.*$string[$"' или конец-строки].*``` и не в двойных кавычках
//       * Замена синтаксическая, подстановка одной строки вместо другой
//       * Имя переменной определяется жадно, берется все до стоп символа ("'$) или окончания токена
//       * Токен - это используемое наименования для обычной строки, просто показывающее, что строка была разделена по определенным правилам
//    4. Подстановка/удаление кавычек. Для одинарных ничего делать не надо,
//       а все спец символы внутри двойных надо экранировать.
//       Удалить сами кавычки, сохранить в виде итоговой, готовой к print строке
//	5. разбивает токены на List[List[str]] по "|"
//	6. Создание Command как объекта (вызов конструктора от List[str]), для каждого элемента списка с прошлого шага
//	7. Возвращает результат работы в формате List[Commands]
//       * Результат разбора (parsing) представляется в формате List[Command], где List отвечает последовательности пайплайновых команд.
//         Поскольку у нас нет вложенных команд, они хранятся индивидуально, в виде Command, а не AST
        List<Command> commands = new ArrayList<>();
        List<String> tokens = tokenize(inputLine);
        if (tokens.size() == 1 && setVarIfNeed(tokens.getFirst(), env)) {
            return new ArrayList<>();
        }
        List<String> singleCommand = new ArrayList<>();
        for (String token : tokens) {
            findVarsAndReplace(token, env);
            if (token.equals("|")) {
                commands.add(new Command(singleCommand));
                singleCommand.clear();
            } else {
                singleCommand.add(evalQuotes(token));
            }
        }
        return commands;
    }

    private static void findVarsAndReplace(String token, Environment env) {
        //TODO
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
        //TODO
    }

    private static List<String> tokenize(String inputLine) {
        List<String> tokens = new ArrayList<>();
        String tokenRegex = "\\||([^|\\s\"']*(\"[^\"]*\"|'[^']*')[^|\\s\"']*)+|[^|\\s\"']+";
        Matcher matcher = Pattern.compile(tokenRegex).matcher(inputLine);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}
