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

package com.github.phonemirror.message;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

/**
 * This message is sent by the PC client to discover phones on the local network. Phones will respond to this message
 * which provides the PC with the IP address to start a TCP connection.
 */
@SuppressWarnings("unused")
public class DiscoveryMessage {

    private int version = 1;
    private int port = -1;
    private String deviceName;
    private int nonce;

    @NotNull
    public static DiscoveryMessage fromJson(@NotNull String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, DiscoveryMessage.class);
    }

    public int getVersion() {
        return version;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return "DiscoveryMessage{" +
                "version=" + version +
                ", port=" + port +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }
}
