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
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class operates in the background to act as a middle man between the phone app and the desktop app.
 * This has several advantages to handling everything in the user-facing app, including keeping the JVM alive
 * to listen for messages from the app and handling them even after the user has closed the user facing app.
 */
public class AppDaemon implements Closeable {

    private static final Logger logger = Logger.getLogger(AppDaemon.class);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private BeaconListener beaconListener;
    private BeaconSender beaconSender;

    @Inject
    public AppDaemon(BeaconListener listener, BeaconSender sender) {
        beaconListener = listener;
        beaconSender = sender;
    }

    @SuppressWarnings("WeakerAccess")
    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            logger.warn("AppDaemon.start() was called, but the thread was already running.");
        }

        startDevicePairingWorker();
        beaconSender.start();
    }

    private void startDevicePairingWorker() {
        System.out.println("START WORKER - PRINT");
        logger.debug("START WORKER - LOG");
        Observable.fromPublisher(beaconListener)
                .subscribeOn(Schedulers.io())
                .subscribe(device -> logger.debug("Found device " + device));
    }

    @Override
    public void close() throws IOException {
        if (!isRunning.compareAndSet(true, false)) {
            logger.warn("AppDaemon was never started, but stop() was called.");
        }

        beaconListener.close();
        beaconSender.close();
    }
}
