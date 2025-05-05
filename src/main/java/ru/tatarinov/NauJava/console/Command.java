package ru.tatarinov.NauJava.console;

import lombok.Getter;

@Getter
public enum Command {
    LIST("list", "Показать все книги"),
    AVAILABLE("available", "Показать доступные книги"),
    ADD("add", "Добавить новую книгу"),
    GET("get", "Найти книгу по ID"),
    UPDATE("update", "Обновить информацию о книге"),
    DELETE("delete", "Удалить книгу"),
    BORROW("borrow", "Взять книгу"),
    RETURN("return", "Вернуть книгу"),
    AUTHOR("author", "Найти книги по автору"),
    HELP("help", "Показать справку"),
    EXIT("exit", "Выйти из программы");

    private final String command;
    private final String description;

    Command(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public static Command fromString(String text) {
        for (Command cmd : Command.values()) {
            if (cmd.command.equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        return null;
    }

    public static String getMenu() {
        StringBuilder sb = new StringBuilder("\nДоступные команды:\n");
        for (Command cmd : Command.values()) {
            sb.append(String.format("%-10s - %s%n", cmd.command, cmd.description));
        }
        return sb.toString();
    }
}