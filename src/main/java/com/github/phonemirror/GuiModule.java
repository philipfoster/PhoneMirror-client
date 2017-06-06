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

import com.github.phonemirror.background.BeaconListener;
import com.github.phonemirror.background.BeaconSender;
import com.github.phonemirror.pojo.PairingData;
import com.github.phonemirror.util.Configuration;
import com.google.gson.Gson;
import com.google.zxing.qrcode.QRCodeWriter;
import dagger.Module;
import dagger.Provides;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * The dagger module for the application
 */
@SuppressWarnings("WeakerAccess")
@Module
public class GuiModule {

    private static final Logger logger = Logger.getLogger(GuiModule.class);


    @Provides
    @Singleton
    public SecureRandom provideRng() {
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Could not create SecureRandom instance", e);
            return null;
        }
    }

    @Provides
    @Singleton
    @Inject
    public BeaconListener provideBeaconListener(Configuration conf, Gson gson) {
        return new BeaconListener(conf, gson);
    }

    @Inject
    @Singleton
    @Provides
    public BeaconSender provideSender(Configuration config, Gson gson) {
        return new BeaconSender(config, gson);
    }

    @Provides
    @Singleton
    @Inject
    public AppDaemon provideDaemon(BeaconListener listener, BeaconSender sender) {
        return new AppDaemon(listener, sender);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }

    @Provides
    public Properties provideProperties() {
        return new Properties();
    }

    @Provides
    @Singleton
    @Inject
    public Configuration provideConfig(Properties props) {
        return new Configuration(props);
    }

    @Provides
    public QRCodeWriter provideQrWriter() {
        return new QRCodeWriter();
    }

    @Provides
    @Inject
    public PairingData providePairingData(Configuration config, SecureRandom rng) {
        return new PairingData(config, rng);
    }
}
