package net.dlagustmd0.mycode.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import net.dlagustmd0.mycode.share.Msg;
import net.dlagustmd0.mycode.share.impls.ChatMsg;
import net.dlagustmd0.mycode.share.impls.ImageMsg;
import net.dlagustmd0.mycode.share.impls.JoinMsg;
import net.dlagustmd0.mycode.share.impls.QuitMsg;

import javax.imageio.ImageIO;
import java.io.File;

public class NettyClientChannelHandler extends SimpleChannelInboundHandler<Msg> {

    public final NettyClient nettyClient;
    public final SocketChannel channel;

    public NettyClientChannelHandler(NettyClient nettyClient, SocketChannel channel) {
        this.nettyClient = nettyClient;
        this.channel = channel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    public void handleMsg(Msg msg) {
        // Spaghetti Code...
        if (msg instanceof JoinMsg) {
            JoinMsg joinMsg = (JoinMsg) msg;
            NettyClient.LOGGER.info("[클라이언트] " + joinMsg.who + "님이 서버에 접속하였습니다.");
        } else if (msg instanceof QuitMsg) {
            QuitMsg quitMsg = (QuitMsg) msg;
            NettyClient.LOGGER.info("[클라이언트] " + quitMsg.who + "님이 서버에서 퇴장하였습니다.");
        } else if (msg instanceof ChatMsg) {
            ChatMsg chatMsg = (ChatMsg) msg;
            NettyClient.LOGGER.info("[채팅] " + chatMsg.sender + ": " + chatMsg.message);
        } else if (msg instanceof ImageMsg) {
            try {
                ImageMsg imageMsg = (ImageMsg) msg;
                File file = new File("image_" + System.currentTimeMillis() + ".jpg");
                ImageIO.write(imageMsg.image, "jpg", file);
                NettyClient.LOGGER.info("[클라이언트] 이미지 파일을 수신했습니다: " + file.getAbsolutePath() + "(으)로 저장되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                NettyClient.LOGGER.fatal("[클라이언트] 이미지 파일을 수신했지만 파일로 저장할 수 없습니다: " + e.getMessage());
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        handleMsg(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        System.exit(0);
    }

    public void sendMsg(Msg msg) {
        channel.writeAndFlush(msg);
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

}
