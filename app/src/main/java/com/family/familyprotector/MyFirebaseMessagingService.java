package com.family.familyprotector;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("FIFI", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.


        //send server
        //sendRegistrationToServer(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d("FIFI", message.getFrom() + "ALDIM");
    }
}
