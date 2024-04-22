package ru.korobtsov.server.handler;

import com.google.common.util.concurrent.Futures;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.factory.StageCommandAbstractFactory;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.stage.StageType;
import ru.korobtsov.server.transport.handler.Response;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Component
public class CommandHandlerManager {

    private final Map<StageType, CommandHandler> commandHandlers;
    private final StageCommandAbstractFactory stageCommandAbstractFactory;

    public CommandHandlerManager(Set<CommandHandler> commandHandlers, StageCommandAbstractFactory stageCommandAbstractFactory) {
        var map = new EnumMap<StageType, CommandHandler>(StageType.class);
        for (var commandHandler : commandHandlers) {
            map.put(commandHandler.stageType(), commandHandler);
        }

        this.commandHandlers = map;
        this.stageCommandAbstractFactory = stageCommandAbstractFactory;
    }

     /**
     * Provides message and context to available handlers and returns response
     *
     * @param message     client message
     * @param gameContext context
     *
     * @return response
     */
    public Response handleMessage(String message, GameContext gameContext) {
        var currentStageLocal = gameContext.currentStage();

        var messageHandler = this.commandHandlers.get(currentStageLocal);
        if (messageHandler == null) {
            return new Response(null, Futures.immediateFuture(HandlingResult.notHandled()));
        }

        var command = stageCommandAbstractFactory.createCommand(message, currentStageLocal);

        return new Response(command.onExecute(gameContext), messageHandler.handleMessage(command, gameContext));
    }
}
