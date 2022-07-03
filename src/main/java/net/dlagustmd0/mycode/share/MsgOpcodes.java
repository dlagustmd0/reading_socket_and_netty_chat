package net.dlagustmd0.mycode.share;

import net.dlagustmd0.mycode.share.impls.ChatMsg;
import net.dlagustmd0.mycode.share.impls.ImageMsg;
import net.dlagustmd0.mycode.share.impls.JoinMsg;
import net.dlagustmd0.mycode.share.impls.QuitMsg;

import java.util.Arrays;

public enum MsgOpcodes {
    JOIN(JoinMsg.class),
    CHAT(ChatMsg.class),
    IMAGE(ImageMsg.class),
    QUIT(QuitMsg.class);

    Class<? extends Msg> msgClass;
    int opcode;

    MsgOpcodes(Class<? extends Msg> msgClass) {
        this.msgClass = msgClass;
        this.opcode = ordinal();
    }

    public static Msg newInstance(int opcode) throws Exception {
        Class<? extends Msg> clazz = fetchClass(opcode);
        return clazz.getConstructor().newInstance();
    }

    public static int getOpcode(Msg msg) {
        return Arrays.stream(MsgOpcodes.values())
                .filter(msgOpcodes -> msgOpcodes.msgClass.getSimpleName().equals(msg.getClass().getSimpleName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(msg.getClass() + "에 해당하는 OpCode를 찾을 수 없습니다."))
                .opcode;
    }

    private static Class<? extends Msg> fetchClass(int opcode) {
        return Arrays.stream(MsgOpcodes.values())
                .filter(msgOpcode -> msgOpcode.opcode == opcode)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(opcode + "에 해당하는 클래스를 찾을 수 없습니다."))
                .msgClass;
    }
}
