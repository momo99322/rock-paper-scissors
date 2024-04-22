package ru.korobtsov.server.stage;

import ru.korobtsov.server.command.CommandType;

public class SearchStage implements Stage {

    @Override
    public Stage prev() {
        return new AuthStage();
    }

    @Override
    public Stage nextSuccessfully() {
        return new GameStage();
    }

    @Override
    public StageType type() {
        return StageType.SEARCH;
    }

    @Override
    public String onSetupMessage() {
        return ("You can find a game with another player with '%s' command. " +
                "You can cancel searching with '%s' command and exit with '%s'.")
                .formatted(
                        CommandType.SEARCH.commandString(),
                        CommandType.CANCEL.commandString(),
                        CommandType.EXIT.commandString()
                );
    }
}
