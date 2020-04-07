package com.family.familyprotector;

import java.util.Date;

public class Message {
    public long time;
    public String unclear = null;
    public String sender = null;
    public String content = null;
    public Date date = null;
    public String saat = null;
    public String type = "simple";
    public Message() {

    }
    public Message(String sender, String content, long time) {
        this.sender = sender;
        this.content = content;
        this.time = time;
    }
    public String toUnique() {
        return sender+content+saat;
    }
    @Override
    public String toString() {
        return sender + ": " + content + " - " + date;
    }
}