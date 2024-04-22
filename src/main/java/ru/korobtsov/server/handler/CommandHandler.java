package ru.korobtsov.server.handler;

import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.stage.StageType;


/**
 * Handles all commands in {@link ru.korobtsov.server.stage.Stage}
 */
public interface CommandHandler {

    /**
     * Handles message if it is possible
     *
     * @param command     command
     * @param gameContext context
     * @return handle result
     */
    ListenableFuture<HandlingResult> handleMessage(@NotNull Command command, @NotNull GameContext gameContext);

    StageType stageType();
}