package net.dlagustmd0.mycode.client;

import net.dlagustmd0.mycode.share.impls.ChatMsg;
import net.dlagustmd0.mycode.share.impls.ImageMsg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Scanner;

public class NettyClientConsoleReader extends Thread {

    public NettyClient nettyClient;
    public NettyClientChannelHandler handler;

    public NettyClientConsoleReader(NettyClient nettyClient, NettyClientChannelHandler handler) {
        this.nettyClient = nettyClient;
        this.handler = handler;
    }

    @Override
    public void run() {
        final Scanner sc = new Scanner(System.in);

        while (true) {
            String line = sc.nextLine();

            try {
                handle(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handle(String line) throws Exception {
        if (line.equals("!q")) {
            this.handler.channel.closeFuture().sync();
            System.exit(0);
        } else if (line.equals("!s")) {
            try {
                Robot robot = new Robot();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                BufferedImage image = robot.createScreenCapture(new Rectangle(screenSize.width, screenSize.height));

                handler.sendMsg(new ImageMsg(image));
            } catch (Exception e) {
                e.printStackTrace();
                NettyClient.LOGGER.fatal("화면을 캡쳐할 수 없습니다: " + e.getMessage());
            }
        } else {
            handler.sendMsg(new ChatMsg(null, line));
        }
    }

}
