package com.family.familyprotector;

public class Message {
    public long time;
    public String unclear = null;
    public String sender = null;
    public String content = null;
    public String type = "simple";
    public Message() {

    }
    public Message(String sender, String content, long time) {
        this.sender = sender;
        this.content = content;
        this.time = time;
    }
}