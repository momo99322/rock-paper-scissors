package ru.korobtsov.server.command;

import ru.korobtsov.server.context.GameContext;

public interface Command {

    CommandType commandType();

    String onExecute(GameContext gameContext);
}
