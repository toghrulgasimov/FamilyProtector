package com.family.familyprotector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Not {
    public static Context context = null;
    public static String CHANNEL_ID = "NOT";
//    public Not(Context c) {
//        this.context = c;
//        createNotification();
//    }
    public static void createNotification(Context c) {
        // disable canceling of notification
        context = c;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Lookin24 Parental Control")
                .setContentText("active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true);
        createNotificationChannel();


        // show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
    private static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    static void update() {
        if(context != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
            context = null;
        }
    }

}
