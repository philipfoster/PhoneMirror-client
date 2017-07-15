package com.github.phonemirror.net.message;

/**
 * A MessageFilter is used with {@link com.github.phonemirror.net.transport.Server}
 * to choose which {@link com.github.phonemirror.net.transport.Server}s
 * should be notified of a new message
 */
@FunctionalInterface
public interface MessageFilter {

    /**
     * Decide whether the message should pass through the filter
     * @param msg the message to test
     * @return {@code true} if it passes, otherwise {@code false}
     */
    boolean filter(Message<?> msg);
}
