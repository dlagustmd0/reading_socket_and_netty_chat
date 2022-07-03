package net.dlagustmd0.mycode.share;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class MsgUtils {

    public static void writeString(ByteBuf buf, String message) {
        if (message == null) {
            buf.writeInt(-1);
            return;
        }

        // 전송을 잘못해서 한글이 안쳐졌구나
        //buf.writeInt(message.length());
        //buf.writeBytes(message.getBytes(StandardCharsets.UTF_8));

        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static String readString(ByteBuf buf) {
        int length = buf.readInt();
        if (length == -1) return null;
        byte[] data = new byte[length];
        buf.readBytes(data);
        return new String(data, StandardCharsets.UTF_8);
    }

}
