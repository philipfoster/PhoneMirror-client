package com.github.phonemirror.background;

import com.github.phonemirror.messaging.Message;
import com.github.phonemirror.messaging.MessageType;
import com.github.phonemirror.pojo.Device;
import com.github.phonemirror.repo.SerialRepository;
import com.github.phonemirror.util.Configuration;
import com.github.phonemirror.util.Startable;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for listening for a multicast search "beacon" from the device, and
 * sending back an acknowledgement. Discovered devices will be published to subscribers. This
 */
public class PairingBeaconListener implements Publisher<Device>, Closeable, Startable {

    private static final Logger logger = Logger.getLogger(PairingBeaconListener.class);
    private static final int BUFFER_SIZE = 512;

    private volatile Map<Subscriber<? super Device>, Subscription> subscriberMap = new ConcurrentHashMap<>();
    private volatile AtomicBoolean isActive = new AtomicBoolean(false);
    private ExecutorService threadPool;
    private Configuration config;
    private Gson gson;
//    private MessageDecoder decoder;
    private SerialRepository serialRepository;


    @Inject
    public PairingBeaconListener(ExecutorService threadPool, Configuration config, Gson gson, SerialRepository repo) {
        this.threadPool = threadPool;
        this.config = config;
        this.gson = gson;
        serialRepository = repo;
//        decoder = new MessageDecoder(gson);
    }


    @Override
    public void subscribe(Subscriber<? super Device> s) {

    }

    @Override
    public void close() throws IOException {
        if (isActive.compareAndSet(true, false)) {
            logger.info("Stopping listener");
            subscriberMap.forEach((subscriber, subscription) -> {
                subscription.cancel();
                subscriber.onComplete();
            });
        } else {
            logger.error("This instance has already been closed");
        }
    }

    /**
     * Listen for beacons and respond to them.
     * Note: This method will block until {@link #close()} is called, so it should be executed on a background
     * thread.
     */
    private void listen() {
        logger.info("Listening for UDP beacons.");
        try(MulticastSocket socket = new MulticastSocket(config.getPort())) {
            socket.joinGroup(config.getMulticastGroup());
            socket.setSoTimeout(config.getTimeout());

            DatagramPacket packet;
            byte[] buf;
            while (isActive.get()) {
                buf = new byte[BUFFER_SIZE];
                packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    logger.info("Received packet");
                } catch (SocketTimeoutException ste) {
                    // Timeouts are expected. The timeout is just set so socket.receive() doesn't
                    // block indefinitely
                    continue;
                }

                try {
                    String data = new String(buf, "US-ASCII");
                    logger.debug(data);
                    Message<String> message = Message.decode(gson, buf);
                    if (message != null && message.getMessageType() == MessageType.NETWORK_SCAN) {

                        sendAcknowledgement(packet.getAddress());
                    }

                } catch (Exception ex) {

                    logger.error("wtf?", ex);
                }
            }

        } catch (IOException ioe) {
            logger.error("An error occurred. Stopping listener.", ioe);
        } finally {
            logger.info("Listener stopped.");
        }
    }

    private void sendAcknowledgement(InetAddress address) {
        logger.info("Sending acknowledgement");
        try (Socket socket = new Socket(address, config.getPort())) {
            socket.setReuseAddress(true);

            //noinspection unchecked
            Message<String> resp = Message.<String>build()
                    .setId(serialRepository.getSerialId())
                    .setType(MessageType.NETWORK_SCAN_ACK)
                    .setPayload(InetAddress.getLocalHost().getHostName())
                    .createMessage();

            byte[] data = gson.toJson(resp, resp.getDataType()).getBytes("US-ASCII");
            logger.debug("message = " + resp.toString());
            socket.getOutputStream().write(data);
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                logger.error("Connection refused by remote machine. IP = " + address);
            }
            logger.error("Socket exception occurred while connecting acknowledging phone. ", e);
        }
    }

    @Override
    public void start() {
        if (isActive.compareAndSet(false, true)) {
            logger.info("Starting listener");
            threadPool.submit(this::listen);
        } else {
            logger.warn("This instance is already running.");
        }
    }
}
