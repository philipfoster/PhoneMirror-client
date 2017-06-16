package com.github.phonemirror.util;

import com.github.phonemirror.pojo.Device;
import com.github.phonemirror.pojo.Message;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

/**
 * This class is responsible for decoding a message received over the network.
 */
public class MessageDecoder {

    private static final Logger logger = Logger.getLogger(MessageDecoder.class);
    private Gson gson;

    /**
     * This method should only be used by Gson to construct a new object when decoding.
     */
    @SuppressWarnings("unused")
    public MessageDecoder() {
        gson = new Gson();
    }

    @Inject
    public MessageDecoder(Gson gson) {
        this.gson = gson;
    }

    /**
     * @param message the message to test
     * @return the type of {@code message}
     */
    @Nullable
    public Message.Type typeOf(@NotNull String message) {
        try {
            Message msg = gson.fromJson(message, Message.class);
            return msg.getType();
        } catch (JsonSyntaxException jse) {
            logger.warn("Received illegally formed JSON." , jse);
            return null;
        }
    }

    /**
     * Decodes the message as a subclass of {@link Message}
     * @param message the message to decode
     * @return the deserialized message
     */
    @Nullable
    public Message decode(@NotNull String message) {
        Message.Type type = typeOf(message);
        if (type == null) {
            return null;
        }
        switch (type) {
            case DEVICE:
                return gson.fromJson(message, Message.class);
            default:
                throw new IllegalArgumentException("This case is not handled in the switch.");
        }
    }

}
