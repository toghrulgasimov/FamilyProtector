package com.family.notofication;

import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {
    public static final String ACTION      = "com.olivierpayen.notificationaccesssample.NOTIFICATION_LISTENER_EXAMPLE";
    public static final String ARG_MESSAGE = "notification_event";
    private final       String TAG         = "salam";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        final String msg = "ID: " + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName();
        Log.d(TAG, msg);
        Intent i = new Intent(ACTION);
        i.putExtra(ARG_MESSAGE, msg);
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}
