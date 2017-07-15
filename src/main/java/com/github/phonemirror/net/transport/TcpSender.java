package com.github.phonemirror.net.transport;

import com.github.phonemirror.net.message.Message;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This class sends a {@link Message} to a remote device via TCP socket.
 */
public class TcpSender {

    private final Logger logger = Logger.getLogger(TcpSender.class);

    private Gson gson;
    private Configuration config;

    @Inject
    public TcpSender(Gson gson, Configuration config) {
        this.gson = gson;
        this.config = config;
    }

    /**
     * Send a message.
     * @param message the message to send
     * @return {@code true} if the message was sent successfully, otherwise {@code false}
     */
    public boolean sendMessage(Message<?> message) {
        try (Socket socket = new Socket(message.getRecipient(), config.getPort())) {
            socket.setReuseAddress(true);

            byte[] data = gson.toJson(message, message.getDataType()).getBytes(StandardCharsets.US_ASCII);
            logger.trace("message = " + message.toString());
            socket.getOutputStream().write(data);
            return true;
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                logger.error("Connection refused by remote machine. IP = " + message.getRecipient());
            } else {
                logger.error("Socket exception occurred while connecting acknowledging phone. ", e);
            }
            return false;
        }
    }
}
