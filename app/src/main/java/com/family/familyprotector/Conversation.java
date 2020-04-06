package com.family.familyprotector;

import java.util.ArrayList;

public class Conversation {
    public String name = null;
    public ArrayList<Message> messages = new ArrayList<>();


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
            Message x = messages.get(i);
            if(x.time == m.time && x.content.equals(m.content)) {
                //same messages
                return;
            }
            if(messages.get(i).time < m.time) {
                messages.add(i+1, m);
                return;
            }
        }
    }

}
