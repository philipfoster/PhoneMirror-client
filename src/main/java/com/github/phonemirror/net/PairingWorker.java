package com.github.phonemirror.net;

import com.github.phonemirror.net.message.KeyRequestType;
import com.github.phonemirror.net.message.Message;
import com.github.phonemirror.net.message.MessageType;
import com.github.phonemirror.net.transport.MulticastServer;
import com.github.phonemirror.net.transport.TcpSender;
import com.github.phonemirror.net.transport.TcpServer;
import com.github.phonemirror.net.transport.TransportListener;
import com.github.phonemirror.repo.SerialRepository;
import com.github.phonemirror.repo.db.dao.DeviceDao;
import com.github.phonemirror.util.Startable;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

/**
 * This class is responsible for the pairing sequence with a device.
 */
public class PairingWorker implements Closeable, Startable {

    private final Logger logger = Logger.getLogger(PairingWorker.class);

    private volatile boolean isRunning = false;
    private final Object runningLock = new Object();
    private final TcpServer server;
    private final TcpSender sender;
    private final MulticastServer mcServer;
    private final ExecutorService threadPool;
    private SerialRepository serial;

    private volatile TransportListener keyRequestListener;
    private volatile TransportListener netScanListener;

    private final DeviceDao deviceDao;

    @Inject
    public PairingWorker(ExecutorService threadPool, TcpServer server, MulticastServer mcSrv, DeviceDao dao,
                         TcpSender sender, SerialRepository repo) {
        this.threadPool = threadPool;
        this.server = server;
        this.mcServer = mcSrv;
        this.deviceDao = dao;
        this.sender = sender;
        this.serial = repo;
    }

    @Override
    public void start() {
        synchronized (runningLock) {
            if (isRunning) {
                logger.warn("PairingWorker is already running. Ignoring call to start().");
                return;
            } else {
                isRunning = true;
            }

            threadPool.submit(this::startKeyRequestListener);
            threadPool.submit(this::startNetworkSearchListener);
        }
    }


    private void startNetworkSearchListener() {
        netScanListener = mcServer.registerListener(msg -> sendAcknowledgement(msg.getRecipient()),
                msg -> msg.getMessageType() == MessageType.NETWORK_SCAN);
    }

    private void sendAcknowledgement(InetAddress recipient) {
        logger.debug("Sending acknowledgement to " + recipient);

        String name;
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.warn("Could not resolve hostname.");
            name = "?";
        }

        sender.sendMessage(Message.<String>build()
                .setRecipient(recipient)
                .setType(MessageType.NETWORK_SCAN_ACK)
                .setPayload(name)
                .setId(serial.getSerialId())
                .createMessage());
    }

    private void startKeyRequestListener() {
        keyRequestListener = server.registerListener(msg -> {
            KeyRequestType type = (KeyRequestType) msg.getPayload();
            switch (type) {
                // generate key and show it to the
                case QR:
                    break;
                case USB:
                    break;
                case PASSWORD:
                    break;
            }
        }, msg -> msg.getMessageType() == MessageType.PAIR_REQUEST_KEY);
    }


    @Override
    public void close() throws IOException {
        synchronized (runningLock) {
            if (isRunning) {
                isRunning = false;
            }

            if (keyRequestListener != null) {
                server.unregisterListener(keyRequestListener);
                keyRequestListener = null;
            }

            if (netScanListener != null) {
                mcServer.unregisterListener(keyRequestListener);
                netScanListener = null;
            }
        }
    }
}
