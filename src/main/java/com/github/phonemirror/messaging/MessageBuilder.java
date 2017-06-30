package com.github.phonemirror.messaging;

public class MessageBuilder<T> {

    private String id;
    private T payload;
    private MessageType type;
    private int version = Message.CURRENT_VERSION;


    MessageBuilder() {}

    public MessageBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public MessageBuilder setPayload(T payload) {
        this.payload = payload;
        return this;
    }

    public MessageBuilder setType(MessageType type) {
        this.type = type;
        return this;
    }

    public MessageBuilder setVersion(int version) {
        this.version = version;
        return this;
    }

    public Message<T> createMessage() {
        return new Message<>(id, payload, type, version);
    }
}