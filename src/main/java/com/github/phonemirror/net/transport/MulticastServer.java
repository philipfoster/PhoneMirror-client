package com.github.phonemirror.net.transport;

import com.github.phonemirror.net.message.Message;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * A MulticastServer listens for multicast messages on the port and group defined by a {@link Configuration}.
 * This occurs in the background and will notify relevant {@link TransportListener}s who have subscribed.
 */
public class MulticastServer extends Server {

    private static final int BUFFER_SIZE = 512;
    private final Logger logger = Logger.getLogger(MulticastServer.class);

    private Gson gson;
    private Configuration config;

    private volatile boolean isRunning = true;
    private final Object runningLock = new Object();

    @Inject
    public MulticastServer(ExecutorService threadPool, Configuration config, Gson gson) {
        this.gson = gson;
        this.config = config;

        threadPool.submit(this::startServer);
    }

    @Override
    protected void startServer() {
        logger.trace("Starting multicast server");

        try (MulticastSocket socket = new MulticastSocket(config.getPort())) {
            socket.joinGroup(config.getMulticastGroup());
            socket.setSoTimeout(config.getTimeout());

            DatagramPacket packet;
            byte[] buf;
            while (checkIsRunning()) {
                buf = new byte[BUFFER_SIZE];
                packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    logger.trace("Received packet");
                } catch (SocketTimeoutException e) {
                    // This is ok - just continue listening.
                    continue;
                }

                try {
                    String data = new String(buf, StandardCharsets.US_ASCII).trim();
                    logger.trace("data received = " + data);

                    Message message = Message.build()
                            .copyFrom(Message.decode(gson, data))
                            .setRecipient(packet.getAddress())
                            .createMessage();

                    logger.trace("Message = " + message);

                    publishMessage(message);
                } catch (Exception ex) {
                    logger.error("Something strange happened. ", ex);
                }
            }

        } catch (IOException e) {
            logger.error(e);
        }

    }

    private boolean checkIsRunning() {
        synchronized (runningLock) {
            return isRunning;
        }
    }

    @Override
    public void close() throws IOException {
        super.close();

        synchronized (runningLock) {
            isRunning = false;
        }
    }
}
