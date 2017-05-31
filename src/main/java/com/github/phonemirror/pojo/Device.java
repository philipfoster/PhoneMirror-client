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

import java.net.InetAddress;

/**
 * This class contains information about a compatible device on the network
 */
public class Device {

    private InetAddress ipAddress;
    private String name;
    private String serialNo;
    private boolean connected;

    private Device(InetAddress ipAddress, String name, String serialNo, boolean connected) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.serialNo = serialNo;
        this.connected = connected;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public String getName() {
        return name;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public String toString() {
        return "Device{" +
                "ipAddress=" + ipAddress +
                ", name='" + name + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", connected=" + connected +
                '}';
    }


    public static class Builder {
        private InetAddress ipAddress;
        private String name;
        private String serialNo;
        private boolean connected;

        public Builder setIpAddress(InetAddress ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSerialNo(String serialNo) {
            this.serialNo = serialNo;
            return this;
        }

        public Builder setConnected(boolean connected) {
            this.connected = connected;
            return this;
        }

        public Device build() {
            return new Device(ipAddress, name, serialNo, connected);
        }
    }
}
