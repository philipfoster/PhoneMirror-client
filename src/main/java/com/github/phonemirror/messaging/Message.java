package com.github.phonemirror.messaging;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

/**
 * This class represents a message which will be sent to a remote device. Each message contains a
 * unique identifier for the device, a {@link MessageType}, protocol version, and payload.
 */
public class Message<T> {

    static final int CURRENT_VERSION = 1;

    private String id;
    private T payload;
    private MessageType type;
    private int version = CURRENT_VERSION;

    Message(String id, T payload, MessageType type, int version) {
        this.id = id;
        this.payload = payload;
        this.type = type;
        this.version = version;
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
     * @param gson An instance of gson to decode with. If {@code null}, a default instance will be created.
     * @param buf the data to decode.
     * @param <R> The type of the payload.
     * @return The decoded message.
     */
    public static <R> Message<R> decode(@Nullable Gson gson, byte[] buf) {
        String data;
        try {
            data = new String(buf, "US-ASCII").trim();
        } catch (UnsupportedEncodingException e) {
            // will never happen on a compliant JVM.
            // see: https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html
            throw new IllegalStateException("Could not decode using ASCII charset.", e);
        }

        if (gson == null) {
            gson = new Gson();
        }

        return gson.fromJson(data, new TypeToken<Message<R>>() {}.getType());
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
                '}';
    }
}
