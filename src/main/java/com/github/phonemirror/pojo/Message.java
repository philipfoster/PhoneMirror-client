package com.github.phonemirror.pojo;

/**
 * This base class represents a basic message which is sent between the PC and phone.
 */
public class Message {

    public enum Type {
        DEVICE
    }

    @SuppressWarnings("WeakerAccess")
    protected Type messageType = null;

    public Message.Type getType() { return messageType; }

}
