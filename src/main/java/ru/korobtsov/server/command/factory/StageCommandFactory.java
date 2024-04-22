package ru.korobtsov.server.command.factory;

import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.stage.StageType;

public interface StageCommandFactory {

    Command create(String message);

    StageType stageType();
}
