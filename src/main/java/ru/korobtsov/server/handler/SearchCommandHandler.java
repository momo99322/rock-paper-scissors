package ru.korobtsov.server.handler;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.command.CommandType;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.stage.StageType;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class SearchCommandHandler extends CancelableCommandHandler {

    private final Exchanger<String> exchanger = new Exchanger<>();

    private final ExecutorService blockingPool;


    public SearchCommandHandler(ExecutorService blockingPool) {
        super(blockingPool);
        this.blockingPool = blockingPool;
    }

    @Override
    protected ListenableFuture<HandlingResult> handleMainTask(Command command, GameContext gameContext) {
        if (command.commandType() == CommandType.SEARCH) {
            return handleSearch(gameContext);
        }

        return Futures.immediateFuture(HandlingResult.notHandled());
    }

    private ListenableFuture<HandlingResult> handleSearch(GameContext gameContext) {
        var nickname = gameContext.nickname();

        return Futures.transformAsync(
                searchOpponent(nickname),
                opponentName -> Futures.immediateFuture(handleOpponent(gameContext, opponentName)),
                blockingPool
        );
    }

    @NotNull
    private ListenableFuture<String> searchOpponent(String nickname) {
        return Futures.submit(() -> {
            try {
                return exchanger.exchange(nickname);
            } catch (InterruptedException e) {
                log.debug("Searching was interrupted, nickname={}", nickname, e);
                return null;
            }
        }, blockingPool);
    }

    @NotNull
    private HandlingResult handleOpponent(GameContext gameContext, @Nullable String opponentNickname) {
        if (opponentNickname == null) {
            var error = new RuntimeException(
                    "Game not found for nickname=%s".formatted(gameContext.nickname())
            );
            return HandlingResult.handledExceptionally("Game hasn't found", error);
        }

        gameContext.opponentNickname(opponentNickname);
        return HandlingResult.handledSuccessfully(
                "Game has found, your opponent is '%s'".formatted(opponentNickname), TransitionType.NEXT
        );
    }

    @Override
    public StageType stageType() {
        return StageType.SEARCH;
    }
}
