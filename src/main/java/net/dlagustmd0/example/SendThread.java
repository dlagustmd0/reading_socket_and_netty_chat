package net.dlagustmd0.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread extends Thread {

    Socket socket;

    SendThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            while (true) {
                String str= reader.readLine();
                if (str.equals("BYE"))
                    break;
                writer.println(str);
                writer.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                System.out.println("***채팅방을 나갑니다***");
                socket.close();
            } catch (Exception e) {
            }
        }
    }

}
