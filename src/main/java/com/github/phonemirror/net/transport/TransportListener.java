package com.github.phonemirror.net.transport;


import com.github.phonemirror.net.message.Message;

/**
 * A TransportListener may subscribe to a {@link Server} for network updates.
 */
@FunctionalInterface
public interface TransportListener {

    void onMessageReceived(Message object);

}
