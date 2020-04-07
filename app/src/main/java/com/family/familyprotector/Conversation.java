package com.family.familyprotector;

import com.family.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Conversation {
    public String name = null;
    public ArrayList<Message> messages = new ArrayList<>();
    public Map<String, Date> M = new HashMap<>();



    public void addAll(ArrayList<Message> l) {
        for(int i = 0; i < l.size(); i++) {
            String us = l.get(i).toUnique();
            if(M.get(us) == null) {
                if(messages.size() > 0) {

                }else if(l.get(i).date != null) {
                    M.put(us, l.get(i).date);
                    messages.add(l.get(i));
                }
            }
        }
    }

}
