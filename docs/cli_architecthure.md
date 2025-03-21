# Архитектура

## Основные компоненты системы
* CLI - основной процесс (main)
* Parser - парсер входной строки (команды)
* Environment - управляет переменными окружения
* Command - способ представления команд
* Pipeline - обрабатывает пайплайны
* Executor - отвечает за исполнение команд

## Используемые классы объектов
* Command:
	* name: str, args: List<Str>, is_builtin?, stdin, stdout, enum/list: builtin_commands
	* constructor, isBuiltin, setStdin, setStdout
	* checkNumberOfArguments for each builtin command
* Environment:
	* vars: map[str, str]
	* getter, setter, isVariable?

## Описание методов остальных классов
* CLI
	1. Создание объекта окружения
	2. Обработка очередной пришедшей строки 
       * Вызов Parser(string, env)
       * Вызов Pipeline(parserResult)
* Parser. Считается, что входная строка - команда (возможно пайплайн) или подстановка переменной
	1. Разбивает строку на токены, с учетом кавычек
	2. Определить если это подстановка переменной. Установить env.setVar
	3. Если команда, выполнить подстановку окружения
	4. разбивает токены на List[List[token/str]] по "|"
	5. Вызов конструктора команд
	6. Возвращает результат работы в формате List[Commands]
* Pipeline
	1. Для каждой команды определяет поток ввода и вывода, например через PipedInputStream
	2. Исполняет каждую команды в отдельном потоке
	3. дожидается завершения всех потоков
* Executor(Command)
	* isBuiltin - специфичный для каждой программы. Executor выполняет переданную команду в своем потоке
	* !isBuiltin - создание нового процесса (ProcessBuilder)
	