package com.github.phonemirror.net.message;


import com.github.phonemirror.repo.SerialRepository;

import java.net.InetAddress;

public class MessageBuilder<T> {

    private String id;
    private T payload;
    private MessageType type;
    private InetAddress address;
    private int version = Message.CURRENT_VERSION;


    MessageBuilder() {}

    public MessageBuilder<T> setId(String id) {
        this.id = id;
        return this;
    }

    public MessageBuilder<T> setRecipient(InetAddress address) {
        this.address = address;
        return this;
    }

    public MessageBuilder<T> copyFrom(Message<T> message) {
        id = message.getId();
        payload = message.getPayload();
        type = message.getMessageType();
        address = message.getRecipient();
        version = message.getVersion();
        return this;
    }

    /**
     * Set the serial ID. When constructing an object for *this* installation to send, this method
     * should be used over {@link #setId(String)}.
     * @param repo the repository
     * @return a reference to this builder for method chaining.
     */
    public MessageBuilder<T> setId(SerialRepository repo) {
        this.id = repo.getSerialId();
        return this;
    }

    public MessageBuilder<T> setPayload(T payload) {
        this.payload = payload;
        return this;
    }

    public MessageBuilder<T> setType(MessageType type) {
        this.type = type;
        return this;
    }

    public MessageBuilder<T> setVersion(int version) {
        this.version = version;
        return this;
    }

    public Message<T> createMessage() {
        return new Message<>(id, payload, type, version, address);
    }
}