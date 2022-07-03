package net.dlagustmd0.mycode.share;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int opcode = byteBuf.readInt();
        Msg msg = MsgOpcodes.newInstance(opcode);
        msg.read(byteBuf);
        list.add(msg);
    }

}
