package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.context.GameContext;

@Component
public class UnsupportedCommand implements Command {

    @Override
    public CommandType commandType() {
        return CommandType.UNSUPPORTED;
    }

    @Override
    public String onExecute(GameContext gameContext) {
        return null;
    }
}
