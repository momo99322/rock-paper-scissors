package ru.korobtsov.server.command;

import ru.korobtsov.server.context.GameContext;

public abstract class MoveCommand implements Command {
    @Override
    public String onExecute(GameContext gameContext) {
        return "Your choice is '%s'. Waiting for opponent move...".formatted(commandType().commandString());
    }
}
