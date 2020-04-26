package com.family.familyprotector;

import android.telephony.mbms.MbmsErrors;

import com.family.util.DoublyLinkedList;
import com.family.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Conversation {
    public String name = null;
    public String number = null;
    public DoublyLinkedList<Message> messages = new DoublyLinkedList<>();
    public Map<String, Calendar> M = new HashMap<>();



    public void addAll(ArrayList<Message> l) {
        ArrayList<Message> first = new ArrayList<>();
        for(int i = 0; i < l.size(); i++) {
            String us = l.get(i).toUnique();
            if(M.get(us) == null) {
                if(messages.size() != 0) {
                    if(l.get(i).date == null) {
                        if(i > 0 && M.containsKey(l.get(i-1).toUnique())) {
                            l.get(i).date = (Calendar) M.get(l.get(i-1).toUnique()).clone();
                            l.get(i).date = StringUtil.setDateTime2(l.get(i).date, l.get(i).saat);
                            if(messages.tail.element.date.getTimeInMillis() <= l.get(i).date.getTimeInMillis() && l.get(i).date.getTimeInMillis() < new Date().getTime()) {
                                messages.addLast(l.get(i));
                                M.put(l.get(i).toUnique(), l.get(i).date);
                            }

                        }
                    }else {
                        Message m = l.get(i);
                        if(m.date.getTimeInMillis() >= messages.tail.element.date.getTimeInMillis() && m.date.getTimeInMillis() < new Date().getTime()){
                            messages.addLast(m);
                            M.put(m.toUnique(), m.date);
                        }else if(m.date.getTimeInMillis() <= messages.head.element.date.getTimeInMillis()) {
                            //messages.addFirst(m);
                            //first.add(m);
                            M.put(m.toUnique(), m.date);
                        }
                    }
                }else if(l.get(i).date != null) {
                    M.put(us, l.get(i).date);
                    messages.addLast(l.get(i));
                }

            }

        }
        for(int i = first.size() - 1; i >= 0; i--) {
            messages.addFirst(first.get(i));
        }

    }

}
