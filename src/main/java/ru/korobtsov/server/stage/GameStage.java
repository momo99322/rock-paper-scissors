package ru.korobtsov.server.stage;

import ru.korobtsov.server.command.CommandType;

public class GameStage implements Stage {

    @Override
    public ru.korobtsov.server.stage.Stage prev() {
        return new SearchStage();
    }

    @Override
    public ru.korobtsov.server.stage.Stage nextSuccessfully() {
        return new CloseStage();
    }

    @Override
    public StageType type() {
        return StageType.GAME;
    }

    @Override
    public String onSetupMessage() {
        return ("You should write '%s', '%s' or '%s' to make move. " +
                "You can cancel your move with '%s' command and exit with '%s'.")
                .formatted(
                        CommandType.ROCK.commandString(),
                        CommandType.PAPER.commandString(),
                        CommandType.SCISSORS.commandString(),
                        CommandType.CANCEL.commandString(),
                        CommandType.EXIT.commandString()
                );
    }
}
