package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.context.GameContext;

@Component
public class SearchCommand implements Command {

    @Override
    public CommandType commandType() {
        return CommandType.SEARCH;
    }

    @Override
    public String onExecute(GameContext gameContext) {
        return "Waiting for the opponent...";
    }

}
