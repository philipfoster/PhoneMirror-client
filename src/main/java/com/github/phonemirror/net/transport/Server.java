package com.github.phonemirror.net.transport;

import com.github.phonemirror.net.message.Message;
import com.github.phonemirror.net.message.MessageFilter;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A Server contains logic to connect with a remote device.
 * <p/>
 * This class is thread-safe
 */
public abstract class Server implements Closeable {

    private final Logger logger = Logger.getLogger(Server.class);

    private final Map<TransportListener, MessageFilter> listeners = Collections.synchronizedMap(new WeakHashMap<>());

    protected abstract void startServer();


    /**
     * This method is to be used by implementing classes to
     * @param message the message to publish
     */
    void publishMessage(Message<?> message) {
        listeners.forEach((k, v) -> {
            if (v.filter(message)) {
                k.onMessageReceived(message);
            }
        });
    }

    /**
     * Begin listening for messages recieved over the network.
     * @param listener the listener to notify
     * @param filter a filter to specify the type of message the listener is interested in
     * @return a reference to the listener
     */
    public TransportListener registerListener(TransportListener listener, MessageFilter filter) {

        if (!listeners.containsKey(listener)) {
            listeners.put(listener, filter);
        } else {
            logger.warn("This listener is already registered.");
        }

        return listener;

    }

    /**
     * Stop listening for new messages
     * @param listener the listener to unregister
     */
    public void unregisterListener(TransportListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void close() throws IOException {
        listeners.clear();
    }
}
