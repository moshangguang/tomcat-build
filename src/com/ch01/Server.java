package com.ch01;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final String strDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final AtomicInteger clientNumber = new AtomicInteger();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();


    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);//<1>
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new Handler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    static class Handler implements Runnable {
        private final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //为请求客户端分配一个编号
                int no = clientNumber.incrementAndGet();
                System.out.println("客户端" + no + "号连接到服务器");
                while (true) {
                    InputStream inputStream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    //读取来自客户端向服务器发送的数据
                    inputStream.read(bytes);
                    String cmd = new String(bytes).trim();
                    OutputStream outputStream = socket.getOutputStream();
                    if ("NOW".equalsIgnoreCase(cmd)) {//客户端请求服务器当前时间
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                        String dateStr = sdf.format(date);
                        outputStream.write(dateStr.getBytes());
                        System.out.println("向客户端" + no + "号输入服务器当前时间:" + dateStr);
                        outputStream.flush();
                    } else if ("SHUTDOWN".equalsIgnoreCase(cmd)) {//客户端请求关闭连接
                        System.out.println("客户端" + no + "号关闭连接");
                        break;
                    } else {//无法执行客户端命令
                        System.out.println("客户端" + no + "号输入错误请求:" + cmd);
                        outputStream.write("NOTHING".getBytes());
                        outputStream.flush();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
