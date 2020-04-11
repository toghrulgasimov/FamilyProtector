package com.family.familyprotector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactHelper {
    Context c;
    public ContactHelper(Context c) {
        this.c = c;
    }
    public void getContactList() {
        Logger.l("CONTACT", "BASHLADI");
        int cnt = 0;
        ContentResolver cr = c.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Logger.l("CONTACT", "Name: " + name);
                        Logger.l("CONTACT", "Name: " + phoneNo);
                        Logger.l("CONTACT", getPhoneNumber(this.c, name) + "-----");
                        cnt++;
                        break;

                    }

                    pCur.close();

                }

            }
        }
        Logger.l("CONTACT", "Qurtardi" + cnt);
        if(cur!=null){
            cur.close();
        }
    }

    public String getPhoneNumber(Context context, String name) {
        String ret = null;

        if(name.contains("'")) return "unsaved";
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if(ret==null)
            ret = "Unsaved";
        return ret;
    }


}
