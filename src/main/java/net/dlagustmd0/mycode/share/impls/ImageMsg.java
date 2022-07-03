package net.dlagustmd0.mycode.share.impls;

import io.netty.buffer.ByteBuf;
import net.dlagustmd0.mycode.share.Msg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageMsg implements Msg {

    public BufferedImage image;

    public ImageMsg() {
    }

    public ImageMsg(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void write(ByteBuf buf) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);

            byte[] data = outputStream.toByteArray();
            buf.writeInt(data.length);
            buf.writeBytes(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read(ByteBuf buf) {
        try {
            int length = buf.readInt();
            byte[] data = new byte[length];
            buf.readBytes(data);
            image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
