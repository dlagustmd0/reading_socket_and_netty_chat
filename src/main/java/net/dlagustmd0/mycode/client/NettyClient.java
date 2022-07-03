package net.dlagustmd0.mycode.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import net.dlagustmd0.mycode.share.MsgDecoder;
import net.dlagustmd0.mycode.share.MsgEncoder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;
import java.util.Properties;

public class NettyClient {

    public static final Logger LOGGER = Logger.getLogger(NettyClient.class);

    private final String ip;
    private final int port;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            // https://stackoverflow.com/questions/29067539/netty-weird-indexoutofboundsexception-readerindex-length-exceeds-writerindex

            new Bootstrap()
                    .option(ChannelOption.AUTO_READ, true)
                    .channel(NioSocketChannel.class)
                    .group(loopGroup)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            NettyClientChannelHandler handler;
                            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(1024 * 256));
                            channel.pipeline()
                                    .addLast("length", new ProtobufVarint32LengthFieldPrepender())
                                    .addLast("length_decode", new ProtobufVarint32FrameDecoder())
                                    .addLast("encoder", new MsgEncoder())
                                    .addLast("decoder", new MsgDecoder())
                                    .addLast("channel_handler", handler = new NettyClientChannelHandler(NettyClient.this, channel));
                            new NettyClientConsoleReader(NettyClient.this, handler).start();
                        }
                    })
                    .connect(this.ip, this.port)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure(getLog4jProperty());

        try {
            NettyClient server = new NettyClient("localhost", 5050);
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
        return NettyClient.class.getResourceAsStream(path);
    }

}
