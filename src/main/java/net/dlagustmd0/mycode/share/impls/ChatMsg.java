package net.dlagustmd0.mycode.share.impls;

import io.netty.buffer.ByteBuf;
import net.dlagustmd0.mycode.share.Msg;
import net.dlagustmd0.mycode.share.MsgUtils;

public class ChatMsg implements Msg {

    public String sender, message;

    public ChatMsg() {
    }

    public ChatMsg(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void write(ByteBuf buf) {
        MsgUtils.writeString(buf, sender);
        MsgUtils.writeString(buf, message);
    }

    @Override
    public void read(ByteBuf buf) {
        sender = MsgUtils.readString(buf);
        message = MsgUtils.readString(buf);
    }

}
