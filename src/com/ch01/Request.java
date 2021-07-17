package com.ch01;

import java.io.IOException;
import java.io.InputStream;

public class Request {
    private static final int BUFFER_SIZE = 2048;
    private InputStream inputStream;
    private String uri;

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void parse() {
        StringBuffer request = new StringBuffer(BUFFER_SIZE);
        byte[] bytes = new byte[BUFFER_SIZE];
        int read;
        try {
            read = inputStream.read(bytes);//<1>
        } catch (IOException e) {
            e.printStackTrace();
            read = -1;
        }
        for (int i = 0; i < read; i++) {
            request.append((char) bytes[i]);
        }
        uri = parseUri(request.toString());
    }

    private String parseUri(String request) {
        int start, end;
        start = request.indexOf(' ');
        if (start == -1) {
            return null;
        }
        end = request.indexOf(' ', start + 1);
        if (end <= start) {
            return null;
        }
        return request.substring(start + 1, end);
    }

    public String getUri() {
        return uri;
    }
}
