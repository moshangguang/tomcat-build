package com.ch01;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 8080);
            while (true) {
                OutputStream outputStream = socket.getOutputStream();
                System.out.print("请输入命令:");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                outputStream.write(line.getBytes());
                outputStream.flush();
                if ("SHUTDOWN".equalsIgnoreCase(line)) {
                    break;
                }
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                inputStream.read(bytes);
                System.out.println("服务端返回:" + new String(bytes).trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
