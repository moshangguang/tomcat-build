package com.ch01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Response {
    private static final int BUFFER_SIZE = 1024;
    private Request request;
    private OutputStream output;

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response(OutputStream output) {
        this.output = output;
    }

    public String parseContentType() {
        if (request.getUri() == null) {
            return null;
        }
        if (request.getUri().endsWith(".html")) {
            return "text/html";
        }
        if (request.getUri().endsWith(".png")) {
            return "image/png";
        }
        return null;
    }

    public String getTime() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        return sdf.format(cd.getTime());
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
                StringBuffer content = new StringBuffer();
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    for (int i = 0; i < ch; i++) {
                        content.append((char) bytes[i]);
                    }
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
                String msg = "HTTP/1.1 200 OK\r\n";
                if (request.getUri().endsWith("png")) {
                    msg += "Accept-Ranges: bytes\r\n";
                }
                msg += "Content-Type: " + parseContentType() + "\r\n" +
                        "Content-length: " + content.length() + "\r\n" +
                        "Date: " + getTime() +
                        "\r\n\r\n" + content;
                output.write(msg.getBytes());
            } else {
                // file not found
                String notFoundMsg = "HTTP/1.1 404 File Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 23\r\n" +
                        "\r\n" +
                        "<h1>File Not Found</h1>";
                output.write(notFoundMsg.getBytes());
            }
            output.flush();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

    }
}
