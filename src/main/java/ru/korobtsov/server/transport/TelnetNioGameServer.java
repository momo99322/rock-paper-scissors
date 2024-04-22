package ru.korobtsov.server.transport;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.korobtsov.server.transport.channel.TelnetChannelInitializer;

@Slf4j
@Component
public class TelnetNioGameServer implements GameServer {
    private final TelnetChannelInitializer channelInitializer;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final int port;

    public TelnetNioGameServer(TelnetChannelInitializer channelInitializer,
                               @Value("${game.server.port:23}") int port,
                               @Value("${game.server.bossPoolSize:1}") int bossPollSize,
                               @Value("${game.server.workerPoolSize:0}") int workerPollSize) {
        this.channelInitializer = channelInitializer;
        this.bossGroup = new NioEventLoopGroup(bossPollSize);
        this.workerGroup = new NioEventLoopGroup(workerPollSize);
        this.port = port;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void start() {
        var serverBootstrap = new ServerBootstrap();

        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.childHandler(channelInitializer);

        var bindFuture = serverBootstrap.bind(port);
        try {
            bindFuture.sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error("Server starting was interrupted, close", e);
            close();
        }
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
