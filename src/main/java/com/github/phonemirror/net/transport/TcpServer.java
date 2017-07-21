package com.github.phonemirror.net.transport;


import com.github.phonemirror.net.message.Message;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;


import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

public class TcpServer extends Server {

    private final Logger logger = Logger.getLogger(TcpServer.class);
    private static final int SOCKET_TIMEOUT_MS = 1000;

    private final Object runningLock = new Object();
    private Configuration config;
    private Gson gson;
    private volatile boolean isRunning = true;

    @Inject
    public TcpServer(ExecutorService threadPool, Configuration config, Gson gson) {
        this.config = config;
        this.gson = gson;

        threadPool.submit(this::startServer);
    }

    @Override
    protected void startServer() {
        try (ServerSocket server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.setSoTimeout(SOCKET_TIMEOUT_MS);
            server.bind(new InetSocketAddress(config.getPort()));

            while (true) {
                synchronized (runningLock) {
                    if (!isRunning) {
                        // stop the server
                        break;
                    }
                }

                try (Socket sock = server.accept()) {
                    sock.setReuseAddress(true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    String data = reader.readLine().trim();
                    logger.trace(String.format("Received data: \n[%s]\n", data));

                    Message message = Message.build()
                            .copyFrom(Message.decode(gson, data))
                            .setRecipient(sock.getInetAddress())
                            .createMessage();

                    publishMessage(message);
                } catch (SocketTimeoutException tex) {
                    // Timing out is expected. Timeout is set to prevent the server from running
                    // indefinitely (until a packet was received) after it was closed.
                } catch (IOException ioe) {
                    logger.error(ioe);
                }

            }

        } catch (IOException e) {
            logger.error(e);
        } finally {
            logger.debug("Server was stopped");
        }
    }


    @Override
    public void close() throws IOException {
        super.close();
        logger.debug("Stopping server.");
        synchronized (runningLock) {
            isRunning = false;
        }
    }
}
