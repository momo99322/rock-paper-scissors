package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.context.GameContext;

@Component
public class ExitCommand implements Command {

    @Override
    public CommandType commandType() {
        return CommandType.EXIT;
    }

    @Override
    public String onExecute(GameContext gameContext) {
        return null;
    }
}
