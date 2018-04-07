package com.codehumane.nio;

import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EchoTest {

    Process server;
    EchoClient client;

    @Before
    public void setUp() throws Exception {
        server = EchoServer.start();
        Thread.sleep(500);
        client = EchoClient.start();
    }

    @Test
    public void sendMessage() throws Exception {

        // when
        val response1 = client.sendMessage("hello");
        val response2 = client.sendMessage("world");

        // then
        assertEquals("hello", response1);
        assertEquals("world", response2);
    }

    @After
    public void tearDown() throws Exception {
        server.destroy();
        EchoClient.stop();
    }
}