package ru.turbogoose.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Connection extends Thread implements AutoCloseable {
    private static final ObjectMapper objectMapper;

    static {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper = new ObjectMapper(jsonFactory);
    }

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final Peer peer;

    public Connection(Socket socket, Peer peer) {
        try {
            this.socket = socket;
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            this.peer = peer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        startRecievingMessages();
    }

    private void startRecievingMessages() {
        try {
            while (true) {
                Message incomingMessage = objectMapper.readValue(in, Message.class);
                peer.processIncomingMessage(incomingMessage);
            }
        } catch (MismatchedInputException ignore) { // another peer disconnected
            peer.removeConnection(this);
        } catch (SocketException ignore) { // socket closed from close()
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void propagate(Message message) {
        try {
            objectMapper.writeValue(out, message);
        } catch (SocketException ignore) { // another peer disconnected
            peer.removeConnection(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
