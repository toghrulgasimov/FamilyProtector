package com.family.familyprotector;

import android.telephony.mbms.MbmsErrors;

import com.family.util.DoublyLinkedList;
import com.family.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Conversation {
    public String name = null;
    public DoublyLinkedList<Message> messages = new DoublyLinkedList<>();
    public Map<String, Date> M = new HashMap<>();



    public void addAll(ArrayList<Message> l) {
        ArrayList<Message> first = new ArrayList<>();
        for(int i = 0; i < l.size(); i++) {
            String us = l.get(i).toUnique();
            if(M.get(us) == null) {
                if(messages.size() != 0) {
                    if(l.get(i).date == null) {
                        if(i > 0 && M.containsKey(l.get(i-1).toUnique())) {
                            l.get(i).date = (Date) M.get(l.get(i-1).toUnique()).clone();
                            StringUtil.setDateTime(l.get(i).date, l.get(i).saat);
                            if(messages.tail.element.date.getTime() <= l.get(i).date.getTime()) {
                                messages.addLast(l.get(i));
                                M.put(l.get(i).toUnique(), l.get(i).date);
                            }

                        }
                    }else {
                        Message m = l.get(i);
                        if(m.date.getTime() >= messages.tail.element.date.getTime()){
                            messages.addLast(m);
                            M.put(m.toUnique(), m.date);
                        }else if(m.date.getTime() <= messages.head.element.date.getTime()) {
                            //messages.addFirst(m);
                            first.add(m);
                            M.put(m.toUnique(), m.date);
                        }
                    }
                }else if(l.get(i).date != null) {
                    M.put(us, l.get(i).date);
                    messages.addFirst(l.get(i));
                }

            }

        }
        for(int i = first.size() - 1; i >= 0; i--) {
            messages.addFirst(first.get(i));
        }

    }

}
