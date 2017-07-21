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
import java.sql.Blob;

/**
 * This class contains information about a compatible device on the network
 */
@SuppressWarnings("WeakerAccess")
public class Device {


    enum Type { PHONE, PC }

    private String name;
    private String serialNo;
    private Type deviceType = Type.PC;

    private transient int id;
    private transient InetAddress ipAddress;
    private transient boolean connected;

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

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return this device's primary key in the database.
     */
    public int getId() {
        return id;
    }

    public byte[] getEncryptionKey() {
        // TODO: implement this
        return new byte[1];
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (isConnected() != device.isConnected()) return false;
        if (getName() != null ? !getName().equals(device.getName()) : device.getName() != null) return false;
        if (getSerialNo() != null ? !getSerialNo().equals(device.getSerialNo()) : device.getSerialNo() != null)
            return false;
        if (deviceType != device.deviceType) return false;
        return getIpAddress() != null ? getIpAddress().equals(device.getIpAddress()) : device.getIpAddress() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getSerialNo() != null ? getSerialNo().hashCode() : 0);
        result = 31 * result + (deviceType != null ? deviceType.hashCode() : 0);
        result = 31 * result + (getIpAddress() != null ? getIpAddress().hashCode() : 0);
        result = 31 * result + (isConnected() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Device{" +
                ", name='" + name + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", deviceType=" + deviceType +
                ", ipAddress=" + ipAddress +
                ", connected=" + connected +
                '}';
    }

    public static class Builder {
        private InetAddress ipAddress;
        private String name;
        private String serialNo;
        private boolean connected = false;

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
