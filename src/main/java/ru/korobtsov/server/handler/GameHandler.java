package ru.korobtsov.server.handler;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.command.CommandType;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.stage.StageType;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static ru.korobtsov.server.handler.GameHandler.Move.*;
import static ru.korobtsov.server.handler.TransitionType.NO_TRANSITION;

@Slf4j
@Component
public class GameHandler extends CancelableCommandHandler {
    private final ExecutorService blockingPool;

    private final Map<String, Move> moves;

    private final Object lock = new Object();

    public GameHandler(ExecutorService blockingPool) {
        super(blockingPool);
        this.blockingPool = blockingPool;
        this.moves = new ConcurrentHashMap<>();
    }

    @Override
    protected ListenableFuture<HandlingResult> handleMainTask(Command command, GameContext gameContext) {
        return switch (command.commandType()) {
            case ROCK, PAPER, SCISSORS -> handleMove(command, gameContext);
            default -> Futures.immediateFuture(HandlingResult.notHandled());
        };
    }

    private ListenableFuture<HandlingResult> handleMove(Command command, GameContext gameContext) {
        Callable<HandlingResult> callable = () -> {
            try {
                var move = fromCommand(command.commandType());
                putAndNotifyAll(gameContext.nickname(), move);

                var opponentMove = blockingGetAndRemove(gameContext.opponentNickname());

                var movesString = "Your move: %s, opponent move: %s".formatted(move.moveString(), opponentMove.moveString());
                return switch (checkWinner(move, opponentMove)) {
                    case WINNER ->
                            HandlingResult.handledSuccessfully("You win! %s".formatted(movesString), TransitionType.NEXT);
                    case LOOSER ->
                            HandlingResult.handledSuccessfully("You loose :( %s".formatted(movesString), TransitionType.NEXT);
                    case DRAW ->
                            HandlingResult.handledSuccessfully("Draw, one more round! %s".formatted(movesString), NO_TRANSITION);
                };
            } catch (InterruptedException e) {
                return HandlingResult.handledSuccessfully("Game was cancelled", TransitionType.PREV);
            }
        };

        return Futures.submit(callable, blockingPool);
    }

    private void putAndNotifyAll(String nickname, Move move) {
        synchronized (lock) {
            moves.put(nickname, move);
            lock.notifyAll();
        }
    }

    @NotNull
    private Move blockingGetAndRemove(String opponentNickname) throws InterruptedException {
        synchronized (lock) {
            Move opponentMove;
            do {
                opponentMove = moves.get(opponentNickname);

                if (opponentMove == null) {
                    lock.wait();
                }

            } while (opponentMove == null);

            return moves.remove(opponentNickname);
        }
    }

    @Override
    public StageType stageType() {
        return StageType.GAME;
    }

    private Resolution checkWinner(Move playerMove, Move opponentMove) {
        if (playerMove == opponentMove) {
            return Resolution.DRAW;
        }

        if (playerMove == ROCK && opponentMove == SCISSORS) {
            return Resolution.WINNER;
        }

        if (playerMove == PAPER && opponentMove == ROCK) {
            return Resolution.WINNER;
        }

        if (playerMove == SCISSORS && opponentMove == PAPER) {
            return Resolution.WINNER;
        }

        return Resolution.LOOSER;
    }

    @Getter
    @Accessors(fluent = true)
    public enum Move {
        UNKNOWN("unknown"),

        ROCK("rock"),

        PAPER("paper"),

        SCISSORS("scissors");

        private final String moveString;

        Move(String moveString) {
            this.moveString = moveString;
        }

        public static Move fromCommand(CommandType commandType) {
            return switch (commandType) {
                case ROCK -> ROCK;
                case PAPER -> PAPER;
                case SCISSORS -> SCISSORS;
                default -> UNKNOWN;
            };
        }
    }

    public enum Resolution {
        WINNER,
        LOOSER,
        DRAW
    }
}
