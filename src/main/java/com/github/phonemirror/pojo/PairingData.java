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

package com.github.phonemirror.pojo;

import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.phonemirror.util.MathUtils.unsign;

/**
 * This data is provided to a mobile device to begin the pairing process.
 */
@SuppressWarnings("FieldCanBeLocal")
public class PairingData implements Serializable {

    private static final Logger logger = Logger.getLogger(PairingData.class);

    private List<String> ipAddresses = new ArrayList<>();
    private int port;
    private String pcName;
    private int version;
    private int nonce;
    private String[] supportedCypherSuites;

    @Inject
    public PairingData(Configuration config, SecureRandom srng) {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.getHardwareAddress() == null) {
                    // filter out unusable interfaces
                    continue;
                }

                Collections.list(networkInterface.getInetAddresses()).stream()
                        // remove IP v6 addresses
                        .filter(addr -> addr instanceof Inet4Address)
                        // remove addresses which aren't in the 192.168.*.* subnet
                        // Java bytes are 8 bit signed 2's complement ints, but getAddress() treats as if it's unsigned,
                        // which can cause a returned array such as [-64, -88, 1, 101]
                        .filter(addr -> unsign(addr.getAddress()[0]) == 192 && unsign(addr.getAddress()[1]) == 168)
                        .forEach(addr -> {
                            ipAddresses.add(addr.getHostAddress());
                            if (pcName == null) {
                                // this should always be the same value for different interfaces or InetAddreses
                                pcName = addr.getHostName();
                            }
                        });
            }

            port = config.getPort();
            version = config.getVersion();
            nonce = srng.nextInt();
        } catch (SocketException e) {
            logger.error(e);
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, PairingData.class);
    }

    public void setSupportedCypherSuites(String[] supportedCypherSuites) {
        this.supportedCypherSuites = supportedCypherSuites;
    }
}
