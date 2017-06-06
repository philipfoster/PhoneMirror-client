package com.github.phonemirror.background;

import com.github.phonemirror.pojo.Device;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for periodically sending the multicast "beacon".
 */
public class BeaconSender implements Closeable {

    private static final Logger logger = Logger.getLogger(BeaconSender.class);
    
    private Configuration config;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private AtomicBoolean isActive = new AtomicBoolean(false);
    private Gson gson;
    private volatile String name = null;

    @Inject
    public BeaconSender(Configuration config, Gson gson) {
        this.config = config;
        this.gson = gson;

        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Could not get device name", e);
        }
    }

    public void start() {
        if (!isActive.compareAndSet(false, true)) {
            logger.warn("BeaconSender is already running but start was called.");
            return;
        }

        executor.scheduleWithFixedDelay(this::sendBeacon, 1, config.getBeaconFrequency(), TimeUnit.SECONDS);
    }
    
    private void sendBeacon() {
        logger.debug("Sending multicast beacon.");
        try (MulticastSocket socket = new MulticastSocket(config.getPort())) {
            socket.joinGroup(config.getMulticastGroup());

            Device send = new Device.Builder()
                    .setConnected(false)
                    .setName(name)
                    .setSerialNo("ASDF")
                    .build();
            String data = gson.toJson(send, Device.class);

            byte[] buf = data.getBytes("US-ASCII");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, config.getMulticastGroup(), config.getPort());

            socket.send(packet);

        } catch (IOException e) {
            logger.error("Could not send beacon", e);
        }
    }
    
    

    @Override
    public void close() throws IOException {
        if (!isActive.compareAndSet(true, false)) {
            executor.shutdown();
        } else {
            logger.warn("BeaconSender.close() was called but it was never started.");
        }
    }
}
