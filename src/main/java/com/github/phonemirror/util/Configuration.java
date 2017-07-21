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

package com.github.phonemirror.util;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * This class loads configuration
 * TODO: document what each parameter means
 */
public class Configuration {

    private Properties defaultProperties;

    @Inject
    public Configuration(Properties props) {
        defaultProperties = props;
        try {
            defaultProperties.load(getClass().getResourceAsStream("/config/defaultconfig.properties"));
        } catch (IOException e) {
            System.err.println("Could not load properties resource.");
            defaultProperties = null;
        }
    }

    public int getDefaultWidth() {
        return Integer.parseInt(defaultProperties.getProperty("defaultWidth"));
    }

    public int getDefaultHeight() {
        return Integer.parseInt(defaultProperties.getProperty("defaultHeight"));
    }

    public int getMinWidth() {
        return Integer.parseInt(defaultProperties.getProperty("minWidth"));
    }

    public int getMinHeight() {
        return Integer.parseInt(defaultProperties.getProperty("minHeight"));
    }

    public int getPort() {
        return Integer.parseInt(defaultProperties.getProperty("port"));
    }

    public URL getMainMenuLayout() {
        return getClass().getResource(defaultProperties.getProperty("mainMenuFxml"));
    }

    public InetAddress getMulticastGroup() throws UnknownHostException {
        return InetAddress.getByName(defaultProperties.getProperty("multicastGroupAddress"));
    }

    public int getVersion() {
        return Integer.parseInt(defaultProperties.getProperty("version"));
    }

    public int getTimeout() {
        return Integer.parseInt(defaultProperties.getProperty("timeout"));
    }

    public long getBeaconFrequency() {
        return Long.parseLong(defaultProperties.getProperty("beaconFrequency"));
    }

    public String getDbName() {
        return defaultProperties.getProperty("dbName");
    }
}
