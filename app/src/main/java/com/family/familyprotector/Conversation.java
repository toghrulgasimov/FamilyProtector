package com.family.familyprotector;

import com.family.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

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
    public void addAll(ArrayList<Message> l) {
        if(l.size() < 3) return;
        for(int i = 0; i < messages.size()-2; i++) {
            if(messages.get(i).content.equals(l.get(0).content) && messages.get(i+1).content.equals(l.get(1).content)&&
                    messages.get(i+2).content.equals(l.get(2).content)) {
                for(int j = 3; j < l.size() && i < messages.size(); j++, i++) {
                    if(!messages.get(i).content.equals(l.get(j).content)) {
                        Date d = messages.get(i+2).date;
                        for(int k = j; k < l.size(); k++) {
                            l.get(k).date = (Date) d.clone();
                            StringUtil.setDateTime(l.get(k).date, l.get(k).unclear);
                        }
                        return;
                    }
                }
            }
        }
    }

}
