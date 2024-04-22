package ru.korobtsov.server.command.factory;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.*;
import ru.korobtsov.server.stage.StageType;

@Component
public class SearchStageCommandFactory implements StageCommandFactory {

    @Override
    public Command create(String message) {
        if (CommandType.SEARCH.commandString().equalsIgnoreCase(message)) {
            return new SearchCommand();
        }

        if (CommandType.CANCEL.commandString().equalsIgnoreCase(message)) {
            return new CancelCommand();
        }

        return new UnsupportedCommand();
    }

    @Override
    public StageType stageType() {
        return StageType.SEARCH;
    }
}
