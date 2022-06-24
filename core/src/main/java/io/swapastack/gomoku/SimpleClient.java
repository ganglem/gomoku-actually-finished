package io.swapastack.gomoku;

import com.google.gson.Gson;
import io.swapastack.gomoku.shared.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import screens.GameScreen;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The SimpleClient extends the WebSocketClient class.
 * The SimpleClient class could be used to establish a WebSocket (ws, wss) connection
 * a WebSocketServer.
 *
 * The send(String message) method could be used to send a String message to the
 * WebSocketServer.
 *
 * note: this client could be used to implement the network standard document.
 *
 * @author Dennis Jehle
 */
public class SimpleClient extends WebSocketClient
{

    // 'Google Gson is a java library that can be used to convert Java Object
    // into their JSON representation.'
    // see: https://github.com/google/gson
    // see: https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types
    private final Gson               gson_;
    public       UUID               userId;
    private       ArrayList<History> history;
    private       boolean            historyReceived;
    private       boolean            goodbyeReceived = false;

    private String lastMessage;


    /**
     * This is the constructor of the SimpleClient class.
     *
     * @param server_uri {@link URI}
     *
     * @author Dennis Jehle
     */
    public SimpleClient(URI server_uri) {

        super(server_uri);
        // create Gson converter
        gson_ = new Gson();
    }


    /**
     * This method is called if the connection to the WebSocketServer is open.
     *
     * @param handshake_data {@link ServerHandshake}
     *
     * @author Dennis Jehle
     */
    @Override
    public void onOpen(ServerHandshake handshake_data) {

        HelloServer helloServer = new HelloServer();
        lastMessage = gson_.toJson(helloServer);
        send(lastMessage);
    }


    /**
     * This method is called if the connection to the WebSocketServer was closed.
     *
     * @param code   status code
     * @param reason the reason for closing the connection
     * @param remote was the close initiated by the remote host
     *
     * @author Dennis Jehle
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {

        System.out.println("closed with exit code "+code+" additional info: "+reason);
    }


    /**
     * This message is called if the WebSocketServer sends a String message to the client.
     *
     * @param message a String message from the WebSocketServer e.g. JSON message
     *
     * @author Dennis Jehle
     */
    @Override
    public void onMessage(String message) {


        lastMessage = message;

        try {

            ExtractorMessage extractorMessage = gson_.fromJson(message, ExtractorMessage.class);
            switch(extractorMessage.messageType) {

                case WelcomeClient:
                    WelcomeClient client = gson_.fromJson(message, WelcomeClient.class);
                    this.userId = client.userId;
                    break;

                case HistoryAll:
                    HistoryAll historyAll = gson_.fromJson(message, HistoryAll.class);
                    history = historyAll.history;
                    historyReceived = true;
                    break;

                case HistorySaved:
                    break;

                case HistoryNotSaved:
                    GameScreen.historySaveError = true;
                    break;

                case GoodbyeClient:
                    goodbyeReceived = true;
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("received message: "+message);
    }


    /**
     * This method is called if the WebSocketServer send a binary message to the client.
     * note: This method is not necessary for this project, because the network standard
     * document specifies a JSON String message protocol.
     *
     * @param message a binary message
     *
     * @author Dennis Jehle
     */
    @Override
    public void onMessage(ByteBuffer message) {
        // do nothing, because binary messages are not supported
    }


    /**
     * This method is called if an exception was thrown.
     *
     * @param exception {@link Exception}
     *
     * @author Dennis Jehle
     */
    @Override
    public void onError(Exception exception) {

        System.err.println("an error occurred:"+exception);
    }

    // custom methods

    public void sendDisconnectMsg() {

        GoodbyeServer goodbyeServer = new GoodbyeServer(userId);
        String message = gson_.toJson(goodbyeServer);
        goodbyeReceived = false;
        send(message);
    }

    /**
     * GoodByeHandshake. Disconnects client from server when returning to MainMenuScreen from GameScreen.
     * Disconnects automatically after 30 seconds if not disconnected before.
     *
     * @author Emilija Kastratovic
     */
    public void disconnect() {

        sendDisconnectMsg();

        try {
            for(int i = 0; i<300; i++) {
                if(goodbyeReceived) {
                    closeBlocking();
                    return;
                }
                Thread.sleep(100);
            }
            closeBlocking();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends Last Game to Server.
     *
     * @param lastGame {@link History}
     *
     * @author Emilija Kastratovic
     */
    public void sendWinner(History lastGame) {

        HistoryPush historyPush = new HistoryPush(userId, lastGame.playerOneName, lastGame.playerTwoName, lastGame.playerOneWinner, lastGame.playerTwoWinner);
        String message = gson_.toJson(historyPush);
        send(message);
    }


    /**
     * Requests Game History.
     *
     * @author Emilija Kastratovic
     */
    public void requestAllHistory() {

        historyReceived = false;
        HistoryGetAll historyGetAll = new HistoryGetAll(userId);
        String message = gson_.toJson(historyGetAll);
        send(message);

        System.out.println("History requested");
    }


    /**
     * @return true if {@link History} is received.
     *
     * @author Emilija Kastratovic
     */
    public boolean isHistoryReceived() {

        System.out.println(historyReceived ? "History received" : "History not received");
        return historyReceived;
    }


    /**
     * @return JSON History {@link ArrayList}.
     *
     * @author Emilija Kastratovic
     */
    public ArrayList<History> getHistory() {

        System.out.println("History returned");
        return history;
    }


    public String getMessage() {

        return this.lastMessage;
    }

}
