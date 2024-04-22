package ru.korobtsov.server.command.factory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.AuthCommand;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.command.UnsupportedCommand;
import ru.korobtsov.server.stage.StageType;

@Component
public class AuthStageCommandFactory implements StageCommandFactory {

    @Override
    public Command create(@NotNull String message) {
        return Strings.isNotBlank(message)
                ? new AuthCommand(message)
                : new UnsupportedCommand();
    }

    @Override
    public StageType stageType() {
        return StageType.AUTH;
    }
}
