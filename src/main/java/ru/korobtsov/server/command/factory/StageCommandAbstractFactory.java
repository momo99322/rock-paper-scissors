package ru.korobtsov.server.command.factory;

import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.command.CommandType;
import ru.korobtsov.server.command.ExitCommand;
import ru.korobtsov.server.command.UnsupportedCommand;
import ru.korobtsov.server.stage.StageType;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory for command that gen
 */
@Component
public class StageCommandAbstractFactory {

    private final Map<StageType, StageCommandFactory> factories;

    public StageCommandAbstractFactory(Set<StageCommandFactory> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(StageCommandFactory::stageType, Function.identity()));
    }

    public Command createCommand(String message, StageType stageType) {
        message = message.trim();

        if (CommandType.EXIT.commandString().equalsIgnoreCase(message)) {
            return new ExitCommand();
        }

        var stageCommandFactory = factories.get(stageType);

        return stageCommandFactory != null
                ? stageCommandFactory.create(message)
                : new UnsupportedCommand();
    }
}
