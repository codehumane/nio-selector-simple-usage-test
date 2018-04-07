package com.codehumane.nio;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

@Slf4j
public class EchoServer {

    public static void main(String[] args) throws IOException {

        val selector = Selector.open();
        val serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 5454));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        val buffer = ByteBuffer.allocate(256);

        while (true) {

            selector.select();
            val selectedKeys = selector.selectedKeys();
            val iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                val key = iterator.next();
                if (key.isAcceptable()) register(selector, serverChannel);
                if (key.isReadable()) answerWithEcho(buffer, key);
                iterator.remove();
            }
        }
    }

    private static void register(Selector selector, ServerSocketChannel socketChannel)
            throws IOException {

        val clientChannel = socketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
        val clientChannel = (SocketChannel) key.channel();
        clientChannel.read(buffer);

        if (new String(buffer.array()).trim().equals("POISON_PILL")) {
            clientChannel.close();
            log.info("Not accepting client message anymore.");
        }

        buffer.flip();
        clientChannel.write(buffer);
        buffer.clear();
    }

    public static Process start() throws IOException, InterruptedException {
        val javaHome = System.getProperty("java.home");
        val javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        val classPath = System.getProperty("java.class.path");
        val className = EchoServer.class.getCanonicalName();

        return new ProcessBuilder(javaBin, "-cp", classPath, className).start();
    }
}
