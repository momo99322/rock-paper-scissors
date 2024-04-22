package ru.korobtsov.server.transport.handler;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.command.CommandType;
import ru.korobtsov.server.context.GameContext;
import ru.korobtsov.server.context.GameContextFactory;
import ru.korobtsov.server.commandhandler.CommandHandlerManager;
import ru.korobtsov.server.commandhandler.HandlingResult;
import ru.korobtsov.server.stage.StageType;

import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class TelnetGameHandler extends SimpleChannelInboundHandler<String> {

    private final CommandHandlerManager commandHandlerManager;

    private final GameContext gameContext;

    private final ExecutorService blockingPool;

    public TelnetGameHandler(CommandHandlerManager commandHandlerManager,
                             GameContextFactory gameContextFactory,
                             ExecutorService blockingPool) {
        this.commandHandlerManager = commandHandlerManager;
        this.gameContext = gameContextFactory.create();
        this.blockingPool = blockingPool;
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {
        log.info("[{}] Client was connected", channelId(ctx));
        gameContext.init();

        ctx.writeAndFlush(Banner.banner());

        // self init
        channelRead0(ctx, CommandType.INIT.commandString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.info("[{}] Read client message='{}'", channelId(ctx), msg);

        var handleMessageResults = commandHandlerManager.handleMessage(msg, gameContext);

        writeAndFlush(handleMessageResults.instantResponse(), ctx);

        Futures.addCallback(
                handleMessageResults.futureResponse(),
                new HandleCallback(msg, gameContext.currentStage(), ctx),
                blockingPool
        );
    }

    private void writeAndFlush(String response, ChannelHandlerContext ctx) {
        if (response == null) {
            return;
        }

        log.info("[{}] Write message='{}'", channelId(ctx), response);
        var future = ctx.writeAndFlush("> " + response + "\r\n");
        try {
            future.sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (gameContext.isClosed()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("[{}] Handles exception in channel, closing", channelId(ctx), cause);
        gameContext.close();
        ctx.close();
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        log.info("[{}] Client was disconnected", channelId(ctx));
        gameContext.close();
        super.channelInactive(ctx);
    }

    @AllArgsConstructor
    private class HandleCallback implements FutureCallback<HandlingResult> {

        private String message;

        private StageType currentState;

        private ChannelHandlerContext ctx;

        @Override
        public void onSuccess(HandlingResult result) {
            if (!result.handled()) {
                log.warn("[{}] Unsupported command in state='{}', clientMessage='{}'", channelId(ctx), currentState, message);
                writeAndFlush("Command '%s' is not supported".formatted(message), ctx);
            }

            if (result.error() != null) {
                log.error("[{}] Error was occurred in state='{}'", channelId(ctx), currentState, result.error());
                writeAndFlush("Something went wrong", ctx);
            }

            writeAndFlush(result.response(), ctx);
            writeAndFlush(gameContext.nextStage(result.transitionType()), ctx);
        }

        @Override
        public void onFailure(@NotNull Throwable t) {
        }
        
    }

    private ChannelId channelId(ChannelHandlerContext ctx) {
        return ctx.channel().id();
    }
}
