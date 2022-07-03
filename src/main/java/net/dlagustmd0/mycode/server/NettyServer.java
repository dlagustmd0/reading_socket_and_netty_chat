package net.dlagustmd0.mycode.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import net.dlagustmd0.mycode.share.Msg;
import net.dlagustmd0.mycode.share.MsgDecoder;
import net.dlagustmd0.mycode.share.MsgEncoder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class NettyServer {

    public static final Logger LOGGER = Logger.getLogger(NettyServer.class);

    private final int port;

    private final List<NettyServerChannelHandler> connections = new CopyOnWriteArrayList<>();

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            // https://stackoverflow.com/questions/29067539/netty-weird-indexoutofboundsexception-readerindex-length-exceeds-writerindex

            new ServerBootstrap()
                    .option(ChannelOption.AUTO_READ, true)
                    .group(loopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(1024 * 256));
                            channel.pipeline()
                                    .addLast("length", new ProtobufVarint32LengthFieldPrepender())
                                    .addLast("length_decode", new ProtobufVarint32FrameDecoder())
                                    .addLast("encoder", new MsgEncoder())
                                    .addLast("decoder", new MsgDecoder())
                                    .addLast("channel_handler", new NettyServerChannelHandler(NettyServer.this, channel));
                        }
                    })
                    .bind(this.port)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }

    public void addConnection(NettyServerChannelHandler handler) {
        synchronized (connections) {
            connections.add(handler);
        }
    }

    public void removeConnection(NettyServerChannelHandler handler) {
        synchronized (connections) {
            connections.remove(handler);
        }
    }

    public void sendToAll(Msg msg) {
        synchronized (connections) {
            connections.forEach(connection -> connection.sendMsg(msg));
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure(getLog4jProperty());

        try {
            NettyServer server = new NettyServer(5050);
            server.start();
        } catch (Exception e) {
            LOGGER.fatal("NettyServer가 오류를 반환했습니다", e);
        }
    }

    public static Properties getLog4jProperty() {
        try {
            Properties properties = new Properties();
            properties.load(getResource("/log4j.properties"));
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("log4j.properties 파일을 불러올 수 없습니다: " + e.getMessage());
        }
    }

    public static InputStream getResource(String path) {
        return NettyServer.class.getResourceAsStream(path);
    }

}
