package com.family.familyprotector;

import java.util.ArrayList;

public class Conversation {
    public String name = null;
    public ArrayList<Message> messages = new ArrayList<>();
    public class Message {
        long time;
        String sender = null;
        String content = null;
        public Message(String sender, String content, long time) {
            this.sender = sender;
            this.content = content;
            this.time = time;
        }
    }

    public void add(Message m) {
        if(messages.size() == 0) {
            messages.add(m);
            return;
        }
        if(m.time > messages.get(messages.size()-1).time) {
            messages.add(m);
            return;
        }
        for(int i = messages.size() - 2; i >= 0; i--) {
            if(messages.get(i).time < m.time) {
                messages.add(i+1, m);
                return;
            }
        }
    }

}
