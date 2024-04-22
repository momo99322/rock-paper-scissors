package ru.korobtsov.server.context;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import ru.korobtsov.server.handler.TransitionType;
import ru.korobtsov.server.stage.CloseStage;
import ru.korobtsov.server.stage.InitStage;
import ru.korobtsov.server.stage.Stage;
import ru.korobtsov.server.stage.StageType;

/**
 * Encapsulates state of game session. Should be unique for every client connection. Blocks every method, that changes state
 */
/* TODO It is not thread safe right now, but should be. You can get current stage, handle client message with that stage,
    somebody changes the stage and you change the stage but already a new one.
    The correct logic is maybe always interrupts previous command on new.
    It's working right now because we shouldn't create a situation when there are two commands in stage that can change the stage in this way
*/
@Slf4j
@Accessors(fluent = true)
public class GameContext {

    @Getter
    @Setter
    private volatile String nickname;

    @Getter
    @Setter
    private volatile String opponentNickname;

    private volatile Stage currentStage;

    public synchronized void init() {
        this.currentStage = new InitStage();
    }

    public synchronized StageType currentStage() {
        return this.currentStage.type();
    }

    /**
     * Change current stage depending on transition type
     *
     * @param transitionType transition type
     * @return stage initial response
     */
    public synchronized String nextStage(TransitionType transitionType) {
        var stateLocalCopy = this.currentStage;

        return switch (transitionType) {
            case NEXT -> {
                this.currentStage = stateLocalCopy.nextSuccessfully();
                yield currentStage.onSetupMessage();
            }
            case PREV -> {
                this.currentStage = stateLocalCopy.prev();
                yield currentStage.onSetupMessage();
            }
            case NO_TRANSITION -> {
                this.currentStage = stateLocalCopy;
                yield null;
            }
            case QUIT -> {
                close();
                yield currentStage.onSetupMessage();
            }
        };
    }

    public synchronized void close() {
        this.currentStage = new CloseStage();
    }

    public synchronized boolean isClosed() {
        return this.currentStage.type() == StageType.CLOSED;
    }
}
