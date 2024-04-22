package ru.korobtsov.server.transport.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.context.GameContextFactory;
import ru.korobtsov.server.handler.CommandHandlerManager;
import ru.korobtsov.server.transport.handler.TelnetGameHandler;

import java.util.concurrent.ExecutorService;

@Component
public class TelnetChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final GameContextFactory gameContextFactory;
    private final CommandHandlerManager commandHandlerManager;
    private final ExecutorService blockingPool;

    private final int maxFrameLengthBytes;

    public TelnetChannelInitializer(GameContextFactory gameContextFactory,
                                    CommandHandlerManager commandHandlerManager,
                                    ExecutorService blockingPool,
                                    @Value("${game.server.maxFrameLengthBytes:8192}") int maxFrameLengthBytes) {
        this.gameContextFactory = gameContextFactory;
        this.commandHandlerManager = commandHandlerManager;
        this.blockingPool = blockingPool;
        this.maxFrameLengthBytes = maxFrameLengthBytes;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) {
        var pipeline = channel.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(maxFrameLengthBytes, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new TelnetGameHandler(commandHandlerManager, gameContextFactory, blockingPool));
    }

}
