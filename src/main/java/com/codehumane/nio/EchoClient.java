package com.codehumane.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

@Slf4j
public class EchoClient {

    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static EchoClient instance;

    public static EchoClient start() {
        if (Objects.isNull(instance)) instance = new EchoClient();
        return instance;
    }

    public static void stop() throws IOException {
        client.close();
        buffer = null;
    }

    private EchoClient() {

        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 5454));
            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            log.error("EchoClient start failed.", e);
        }
    }

    public String sendMessage(String message) {
        buffer = ByteBuffer.wrap(message.getBytes());
        String response = null;

        try {
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            response = new String(buffer.array()).trim();
            log.info("response=" + response);
            buffer.clear();
        } catch (IOException e) {
            log.error("Message send failed.", e);
        }

        return response;
    }
}
