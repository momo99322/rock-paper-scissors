package ru.korobtsov.server.command;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum CommandType {
    UNSUPPORTED(null),
    INIT("init"),
    AUTH(null),
    SEARCH("search"),
    CANCEL("cancel"),
    ROCK("rock"),
    PAPER("paper"),
    SCISSORS("scissors"),

    EXIT("exit");

    private final String commandString;

    CommandType(String commandString) {
        this.commandString = commandString;
    }
}
