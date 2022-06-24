package io.swapastack.gomoku;

import com.google.gson.Gson;
import io.swapastack.gomoku.shared.GoodbyeClient;
import io.swapastack.gomoku.shared.WelcomeClient;
import junit.framework.TestCase;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tests for {@link SimpleClient}.
 */
public class SimpleClientTest extends TestCase
{

    /**
     * Tests the {@link SimpleClient} onOpen method.
     * Tests the WelcomeClient Message.
     * @throws URISyntaxException
     *
     * @author Emilija Kastratovic
     */
    public void testOnOpen() throws URISyntaxException {

        Gson   gson       = new Gson();
        String hostname   = "localhost";
        int    port       = 42005;
        URI    server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer test_server = new GomokuServer(new InetSocketAddress(hostname, port));
        test_server.setReuseAddr(true);
        test_server.setTcpNoDelay(true);
        test_server.start();

        try {
            Thread.sleep(200);
            SimpleClient testClient = new SimpleClient(server_uri);
            testClient.connectBlocking();
            Thread.sleep(200);
            String msg = testClient.getMessage();
            WelcomeClient helloServer = new WelcomeClient(testClient.userId, "Hi!");
            testClient.disconnect();
            assertEquals(msg, gson.toJson(helloServer));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Tests the {@link SimpleClient} onClose method.
     * Tests the GoodBye Message.
     * @throws URISyntaxException
     *
     * @author Emilija Kastratovic
     */
    public void testOnClose() throws URISyntaxException {

        Gson   gson       = new Gson();
        String hostname   = "localhost";
        int    port       = 42005;
        URI    server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer test_server = new GomokuServer(new InetSocketAddress(hostname, port));
        test_server.setReuseAddr(true);
        test_server.setTcpNoDelay(true);
        test_server.start();

        try {
            Thread.sleep(200);
            SimpleClient testClient = new SimpleClient(server_uri);
            testClient.connect();
            Thread.sleep(200);
            GoodbyeClient goodbyeServer = new GoodbyeClient("Bye!");
            testClient.disconnect();
            String msg = testClient.getMessage();
            assertEquals(msg, gson.toJson(goodbyeServer));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Tests the {@link SimpleClient} onMessage method.
     * Tests the GoodBye Message.
     * @throws URISyntaxException
     *
     * @author Emilija Kastratovic
     */
    public void testOnMessage() throws URISyntaxException {

        Gson   gson       = new Gson();
        String hostname   = "localhost";
        int    port       = 42005;
        URI    server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer test_server = new GomokuServer(new InetSocketAddress(hostname, port));
        test_server.setReuseAddr(true);
        test_server.setTcpNoDelay(true);
        test_server.start();

        try {
            Thread.sleep(200);
            SimpleClient testClient = new SimpleClient(server_uri);
            testClient.connectBlocking();
            Thread.sleep(200);
            String msg = testClient.getMessage();
            WelcomeClient helloServer = new WelcomeClient(testClient.userId, "Hi!");
            testClient.disconnect();
            assertEquals(msg, gson.toJson(helloServer));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}