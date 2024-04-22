package ru.korobtsov.server.handler;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.context.GameContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static ru.korobtsov.server.handler.TransitionType.NO_TRANSITION;

public abstract class CancelableCommandHandler implements CommandHandler {

    private final ExecutorService blockingExecutor;

    private final Map<String, ListenableFuture<HandlingResult>> tasks;

    public CancelableCommandHandler(ExecutorService blockingExecutor) {
        this.blockingExecutor = blockingExecutor;
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public ListenableFuture<HandlingResult> handleMessage(@NotNull Command command, @NotNull GameContext gameContext) {
        return switch (command.commandType()) {
            case CANCEL -> handleCancel(gameContext);
            case EXIT -> handleQuit(gameContext);
            case UNSUPPORTED -> Futures.immediateFuture(HandlingResult.notHandled());
            default -> {
                if (tasks.containsKey(gameContext.nickname())) {
                    yield Futures.immediateFuture(HandlingResult.handledSuccessfully(
                            "Command '%s' is already in processing status".formatted(command.commandType().commandString()),
                            NO_TRANSITION)
                    );
                }

                var mainTask = handleMainTask(command, gameContext);
                mainTask.addListener(() -> tasks.remove(gameContext.nickname()), blockingExecutor);

                tasks.put(gameContext.nickname(), mainTask);

                yield mainTask;
            }
        };
    }

    protected abstract ListenableFuture<HandlingResult> handleMainTask(Command command, GameContext gameContext);

    protected ListenableFuture<HandlingResult> handleQuit(GameContext gameContext) {
        var nickname = gameContext.nickname();
        var task = tasks.get(nickname);

        if (task != null) {
            task.cancel(true);
        }

        return Futures.immediateFuture(HandlingResult.handledQuit());
    }

    protected ListenableFuture<HandlingResult> handleCancel(GameContext gameContext) {
        var nickname = gameContext.nickname();
        var task = tasks.remove(nickname);

        if (task == null) {
            return Futures.immediateFuture(
                    HandlingResult.handledSuccessfully("Command is not executing", NO_TRANSITION)
            );
        }

        task.cancel(true);

        return Futures.immediateFuture(
                HandlingResult.handledSuccessfully("Command was cancelled", NO_TRANSITION)
        );
    }
}
