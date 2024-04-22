package ru.korobtsov.server.command.factory;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.*;
import ru.korobtsov.server.stage.StageType;

@Component
public class GameStageCommandFactory implements StageCommandFactory {

    @Override
    public Command create(String message) {
        if (CommandType.ROCK.commandString().equalsIgnoreCase(message)) {
            return new RockCommand();
        }

        if (CommandType.PAPER.commandString().equalsIgnoreCase(message)) {
            return new PaperCommand();
        }

        if (CommandType.SCISSORS.commandString().equalsIgnoreCase(message)) {
            return new ScissorsCommand();
        }

        if (CommandType.CANCEL.commandString().equalsIgnoreCase(message)) {
            return new CancelCommand();
        }

        return new UnsupportedCommand();
    }

    @Override
    public StageType stageType() {
        return StageType.GAME;
    }
}
