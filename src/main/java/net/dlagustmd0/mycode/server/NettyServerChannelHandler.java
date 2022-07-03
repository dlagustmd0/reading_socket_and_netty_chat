package net.dlagustmd0.mycode.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import net.dlagustmd0.mycode.share.Msg;
import net.dlagustmd0.mycode.share.impls.ChatMsg;
import net.dlagustmd0.mycode.share.impls.ImageMsg;
import net.dlagustmd0.mycode.share.impls.QuitMsg;
import net.dlagustmd0.mycode.share.impls.JoinMsg;

import java.net.SocketAddress;

public class NettyServerChannelHandler extends SimpleChannelInboundHandler<Msg> {

    public final NettyServer nettyServer;
    public final SocketChannel channel;

    public NettyServerChannelHandler(NettyServer nettyServer, SocketChannel channel) {
        this.nettyServer = nettyServer;
        this.channel = channel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        nettyServer.addConnection(this);

        SocketAddress address = ctx.channel().remoteAddress();
        NettyServer.LOGGER.info("[서버] " + address.toString() + "님이 서버에 접속하였습니다.");
        nettyServer.sendToAll(new JoinMsg(address.toString()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        nettyServer.removeConnection(this);

        SocketAddress address = ctx.channel().remoteAddress();
        NettyServer.LOGGER.info("[서버] " + address.toString() + "님이 서버에서 퇴장하였습니다.");
        nettyServer.sendToAll(new QuitMsg(address.toString()));
    }

    public void handleMsg(Msg msg, ChannelHandlerContext ctx) {
        if (msg instanceof ChatMsg) {
            sendToAll(new ChatMsg(ctx.channel().remoteAddress().toString(), ((ChatMsg) msg).message));
        } else if (msg instanceof ImageMsg) {
            sendToAll(msg); // 모두에게 전송
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        handleMsg(msg, channelHandlerContext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        nettyServer.removeConnection(this);
    }

    public void sendMsg(Msg msg) {
        channel.writeAndFlush(msg);
    }

    public void sendToAll(Msg msg) {
        nettyServer.sendToAll(msg);
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

}
