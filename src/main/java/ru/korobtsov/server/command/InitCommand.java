package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.context.GameContext;

@Component
public class InitCommand implements Command {

    @Override
    public CommandType commandType() {
        return CommandType.INIT;
    }

    @Override
    public String onExecute(GameContext gameContext) {
        return null;
    }
}
