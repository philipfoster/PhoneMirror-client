/*
 * PhoneMirror-client
 * Copyright (C) 2017  Philip Foster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.phonemirror.background;

import com.github.phonemirror.pojo.Device;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manages connections between the PC and relevant devices on the network.
 */
public class DevicePairingWorker implements Publisher<Device>, Closeable {

    private static final Logger logger = Logger.getLogger(DevicePairingWorker.class);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Configuration config;
    private List<Subscriber<? super Device>> subscribers = new ArrayList<>();
    private Gson gson;

    @Inject
    public DevicePairingWorker(Configuration config, Gson gson) {
        this.config = config;
        this.gson = gson;
    }

    private void startBeaconListener() {


        try (MulticastSocket socket = new MulticastSocket(config.getPort())) {
            logger.info(socket.getInterface().toString());
            socket.joinGroup(config.getMulticastGroup());
            socket.setSoTimeout(config.getTimeout());

            while (running.get()) {
                logger.debug("RUN");
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException ste) {
                    continue;
                }

                try {
                    String data = new String(packet.getData());
                    Device recv = gson.fromJson(data, Device.class);

                    if (recv != null) {
                        logger.info("Received packet: + \n" + recv.toString());
                        subscribers.forEach(sub -> sub.onNext(recv));
                    } else {
                        logger.info("Received unknown packet: \n" + data);
                    }

                } catch (JsonSyntaxException jse) {
                    logger.warn("A non-compliant multicast message was received.", jse);
                }
            }

            subscribers.forEach(Subscriber::onComplete);
        } catch (IOException e) {
            logger.error("Could not create socket.", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (!running.compareAndSet(true, false)) {
            logger.error("close was already called");
        }
    }

    @Override
    public void subscribe(Subscriber<? super Device> subscriber) {
        subscribers.add(subscriber);
        if (!running.getAndSet(true)) {
            startBeaconListener();
        }
    }
}