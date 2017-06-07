package com.sh.campaign.runtime.kafka;

import java.io.Serializable;
import java.util.Date;

public class Message<T extends Serializable> {

	private String Id;
	private long messageTime;
	private T payload;
	
	public Message() {
		messageTime = System.currentTimeMillis();
	}

    public T getPayload() {
        return payload;
    }

    public void setPayload(T messageBody) {
        this.payload = messageBody;
    }

	public long getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}
	
	@Override
	public String toString() {
		return "[" + new Date(this.messageTime).toString() + "] " + this.payload.toString();
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
}