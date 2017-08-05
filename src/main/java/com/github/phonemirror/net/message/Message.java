package com.github.phonemirror.net.message;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;

/**
 * This class represents a message which will be sent to a remote device. Each message contains a
 * unique identifier for the device, a {@link MessageType}, protocol version, and payload.
 * TODO: support encryption
 */
public class Message<T> {

    private static final Logger logger = Logger.getLogger(Message.class);
    static final int CURRENT_VERSION = 1;

    private String id;
    private T payload;
    private MessageType type;
    private int version = CURRENT_VERSION;

    private transient InetAddress recipient;

    Message(String id, T payload, MessageType type, int version, InetAddress address) {
        this.id = id;
        this.payload = payload;
        this.type = type;
        this.version = version;
        recipient = address;
    }

    /**
     * Create a builder instance.
     * @param <R> The type of message payload.
     */
    public static <R> MessageBuilder<R> build() {
        return new MessageBuilder<>();
    }

    /**
     * Decode a message.
     * @param <R> The type of the payload.
     * @param gson An instance of gson to decode with. If {@code null}, a default instance will be created.
     * @param buf the data to decode.
     * @return The decoded message. or {@code null} if it couldn't be decoded.
     */
    @Nullable
    public static <R> Message<R> decode(@Nullable Gson gson, String buf) {

        // TODO: handle decrypting messages that require it.

        // Only the payload field needs to be encrypted, the rest should be sent in plaintext
        // this will possibly require a wrapper class for the payload field with some extra metadata,
        // and writing a custom gson TypeAdapterFactory for it.  https://stackoverflow.com/a/26596808

        try {
            if (gson == null) {
                gson = new Gson();
            }

            return gson.fromJson(buf, new TypeToken<Message<R>>() {
            }.getType());
        } catch (JsonSyntaxException jse) {
            logger.warn("Could not decode json message. ", jse);
            return null;
        }
    }


    public String getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }

    public MessageType getMessageType() {
        return type;
    }

    public int getVersion() {
        return version;
    }

    public Class<? super Message<T>> getDataType() {
        return new TypeToken<Message<T>>() {}.getRawType();
    }


    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", payload=" + payload +
                ", type=" + type +
                ", version=" + version +
                ", recipient=" + recipient +
                '}';
    }

    public InetAddress getRecipient() {
        return recipient;
    }
}
