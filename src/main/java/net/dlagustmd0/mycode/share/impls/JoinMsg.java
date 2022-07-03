package net.dlagustmd0.mycode.share.impls;

import io.netty.buffer.ByteBuf;
import net.dlagustmd0.mycode.share.Msg;
import net.dlagustmd0.mycode.share.MsgUtils;

public class JoinMsg implements Msg {

    public String who;

    public JoinMsg() {
    }

    public JoinMsg(String who) {
        this.who = who;
    }

    @Override
    public void write(ByteBuf buf) {
        MsgUtils.writeString(buf, who);
    }

    @Override
    public void read(ByteBuf buf) {
        who = MsgUtils.readString(buf);
    }

}
