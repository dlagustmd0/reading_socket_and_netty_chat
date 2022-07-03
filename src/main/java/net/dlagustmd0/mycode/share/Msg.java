package net.dlagustmd0.mycode.share;

import io.netty.buffer.ByteBuf;

public interface Msg {

    void write(ByteBuf buf);
    void read(ByteBuf buf);

}
