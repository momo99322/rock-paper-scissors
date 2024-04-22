package ru.korobtsov.server.commandhandler;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.stage.StageType;

@Component
public class InitCommandHandler implements CommandHandler {

    @Override
    public ListenableFuture<HandlingResult> handleMessage(@NotNull Command command, @NotNull GameContext gameContext) {
        return Futures.immediateFuture(HandlingResult.handledSuccessfully(null, TransitionType.NEXT));
    }

    @Override
    public StageType stageType() {
        return StageType.INIT;
    }
}
