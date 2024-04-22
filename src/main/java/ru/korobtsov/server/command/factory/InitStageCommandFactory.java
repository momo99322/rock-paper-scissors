package ru.korobtsov.server.command.factory;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.command.CommandType;
import ru.korobtsov.server.command.InitCommand;
import ru.korobtsov.server.command.UnsupportedCommand;
import ru.korobtsov.server.stage.StageType;

@Component
public class InitStageCommandFactory implements StageCommandFactory {

    @Override
    public Command create(String message) {
        return CommandType.INIT.commandString().equalsIgnoreCase(message)
                ? new InitCommand()
                : new UnsupportedCommand();
    }

    @Override
    public StageType stageType() {
        return StageType.INIT;
    }
}
