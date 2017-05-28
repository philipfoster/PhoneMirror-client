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

package com.github.phonemirror.net;

import com.github.phonemirror.message.DiscoveryMessage;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is responsible for scanning for nearby devices that we can connect to.
 */
public class DeviceProbe {

    private final Logger logger = Logger.getLogger(DeviceProbe.class);
    private Gson gson;
    private Configuration config;

    /**
     * Create a {@code DeviceProbe}
     */
    @Inject
    public DeviceProbe(Configuration config, Gson gson) {
        this.config = config;
        this.gson = gson;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendBeacon();
            }
        }, 0, 1000);

        sendBeacon();
    }

    /**
     * Send a UDP multicast "ping" to let devices on the network know that this PC is available to connect to.
     * Devices which connect to this will respond with a TCP response which will be handled elsewhere in this
     * program.
     */
    private void sendBeacon() {
        try (MulticastSocket sock = new MulticastSocket(config.getPort())) {
            InetAddress group = config.getMulticastAddress();
            sock.joinGroup(group);
            String json = gson.toJson(buildMessage());
            byte[] data = json.getBytes("US-ASCII");
            sock.send(new DatagramPacket(data, data.length, group, config.getPort()));

        } catch (IOException e) {
            logger.error("A network error occurred", e);
        }
    }


    private DiscoveryMessage buildMessage() {
        DiscoveryMessage message = new DiscoveryMessage();
        try {
            message.setDeviceName(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
            logger.warn("Could not resolve local host name. Providing default value.", ex);
            message.setDeviceName("unknown name");
        }
        message.setPort(config.getPort());
        return message;
    }

//    /**
//     * This method asynchronously scans the local network for devices to connect to.
//     *
//     * @param subscriber A callback which this method will inform
//     */
//    public void scanNetworkAsync(Subscriber<InetAddress> subscriber) {
//        byte[] sendBuffer = new byte[256];
//        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length);
//        sendPacket.setData(SEND_MSG.getBytes());
//
//        try {
//            socket.send(sendPacket);
//        } catch (IOException e) {
//            e.printStackTrace();
//            subscriber.onError(e.fillInStackTrace());
//            return;
//        }
//
//        executorService.submit(() -> {
//            long start = System.currentTimeMillis();
//            byte[] buffer = new byte[256];
//            DatagramPacket packet;
//            while ((System.currentTimeMillis() - start) < 10000) { // run a maximum of 10 seconds
//                packet = new DatagramPacket(buffer, buffer.length);
//                try {
//                    socket.receive(packet);
//                    String recv = new String(packet.getData());
//                    recv = recv.trim().toLowerCase();
//
//                    if (recv.equals(RECV_MSG)) {
//                        subscriber.onNext(packet.getAddress());
//                    }
//                } catch (IOException e) {
//                    //TODO: log this
//                    e.printStackTrace();
//                    subscriber.onError(e.fillInStackTrace());
//                    return;
//                }
//            }
//
//            subscriber.onComplete();
//        });
//    }
}
