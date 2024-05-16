package ru.turbogoose.core;

import ru.turbogoose.console.ConsoleRenderer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Peer extends Thread {
    private final String name;
    private final int port;

    private ServerSocket serverSocket;
    private final List<Connection> connections = Collections.synchronizedList(new LinkedList<>());
    private final MessageLog messageLog = new MessageLog();
    private ConsoleRenderer renderer;

    public Peer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public void setRenderer(ConsoleRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Connection connection = new Connection(serverSocket.accept(), this);
                connections.add(connection);
                connection.start();
            }
        } catch (SocketException ignore) { // socket closed from shutdown()
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(String host, int port) {
        try {
            Connection connection = new Connection(new Socket(host, port), this);
            connections.add(connection);
            connection.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    public void shutdown() {
        try {
            for (Connection con : connections) {
                con.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        Message msg = new Message(message, name);
        messageLog.addMessage(msg);
        renderMessageLog();
        propagateMessage(msg);
    }

    void processIncomingMessage(Message message) {
        if (!messageLog.contains(message)) {
            messageLog.addMessage(message);
            renderMessageLog();
            propagateMessage(message);
        }
    }

    private void propagateMessage(Message message) {
        for (Connection con : connections) {
            con.propagate(message);
        }
    }

    private void renderMessageLog() {
        if (renderer != null) {
            renderer.render(messageLog);
        }
    }
}
