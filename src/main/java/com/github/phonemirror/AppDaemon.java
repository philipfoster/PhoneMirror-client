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

package com.github.phonemirror;


import com.github.phonemirror.net.message.Message;
import com.github.phonemirror.net.message.MessageType;
import com.github.phonemirror.net.transport.MulticastServer;
import com.github.phonemirror.net.transport.TcpSender;
import com.github.phonemirror.net.transport.TransportListener;
import com.github.phonemirror.repo.SerialRepository;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class operates in the net to act as a middle man between the phone app and the desktop app.
 * This has several advantages to handling everything in the user-facing app, including keeping the JVM alive
 * to listen for messages from the app and handling them even after the user has closed the user facing app.
 */
public class AppDaemon implements Closeable {

    private static final Logger logger = Logger.getLogger(AppDaemon.class);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private MulticastServer server;
    private SerialRepository serial;
    private TcpSender sender;
    private TransportListener multicastListener;

    @Inject
    public AppDaemon(MulticastServer server, TcpSender sender, SerialRepository serial) {
        this.server = server;
        this.sender = sender;
        this.serial = serial;
        start();
    }

    @SuppressWarnings("WeakerAccess")
    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            logger.warn("AppDaemon.start() was called, but the thread was already running.");
        } else {
            startDevicePairingWorker();
        }
    }

    private void startDevicePairingWorker() {
        logger.debug("START WORKER - LOG");

        multicastListener = msg -> sendAcknowledgement(msg.getRecipient());
        server.registerListener(multicastListener, msg -> msg.getMessageType() == MessageType.NETWORK_SCAN);
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

    @Override
    public void close() throws IOException {
        if (!isRunning.compareAndSet(true, false)) {
            logger.warn("AppDaemon was never started, but stop() was called.");
        } else {
            server.unregisterListener(multicastListener);
        }

    }
}
