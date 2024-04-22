package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.context.GameContext;

@Component
public class CancelCommand implements Command {

    @Override
    public CommandType commandType() {
        return CommandType.CANCEL;
    }

    @Override
    public String onExecute(GameContext gameContext) {
        return "Canceling command...";
    }
}
