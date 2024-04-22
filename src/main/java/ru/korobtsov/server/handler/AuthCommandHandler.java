package ru.korobtsov.server.handler;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.auth.AuthenticationResult;
import ru.korobtsov.server.auth.Principal;
import ru.korobtsov.server.auth.service.AuthenticationService;
import ru.korobtsov.server.command.AuthCommand;
import ru.korobtsov.server.command.Command;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.stage.StageType;

import java.util.concurrent.ExecutorService;

@Component
public class AuthCommandHandler implements CommandHandler {

    private final AuthenticationService authenticationService;

    private final ExecutorService blockingPool;

    public AuthCommandHandler(AuthenticationService authenticationService, ExecutorService blockingPool) {
        this.authenticationService = authenticationService;
        this.blockingPool = blockingPool;
    }

    @Override
    public ListenableFuture<HandlingResult> handleMessage(@NotNull Command command, @NotNull GameContext gameContext) {
        return switch (command.commandType()) {
            case AUTH -> handleAuth(command, gameContext);
            case EXIT -> handleQuit();
            default -> Futures.immediateFuture(HandlingResult.notHandled());
        };
    }

    private ListenableFuture<HandlingResult> handleAuth(@NotNull Command command, GameContext gameContext) {
        var authCommand = (AuthCommand) command;
        var nickname = authCommand.nickname();


        return Futures.transformAsync(
                authenticationService.authenticate(new Principal(nickname)),
                authenticationResult -> Futures.immediateFuture(handleAuthenticationResult(authCommand, gameContext, authenticationResult)),
                blockingPool
        );
    }

    private ListenableFuture<HandlingResult> handleQuit() {
        return Futures.immediateFuture(HandlingResult.handledQuit());
    }

    @NotNull
    private HandlingResult handleAuthenticationResult(@NotNull AuthCommand command,
                                                      @NotNull GameContext gameContext,
                                                      AuthenticationResult authenticationResult) {
        var nickname = command.nickname();
        if (!authenticationResult.authenticated()) {
            var error = new IllegalArgumentException(
                    "Player hasn't authenticated, nickname=%s".formatted(nickname)
            );

            return HandlingResult.handledExceptionally("Authentication error", error);
        }

        gameContext.nickname(nickname);

        return HandlingResult.handledSuccessfully("Welcome, %s!".formatted(nickname), TransitionType.NEXT);
    }
    @Override
    public StageType stageType() {
        return StageType.AUTH;
    }
}
