package com.AlForce.android.runvolution.messaging;

import android.util.Log;

import com.AlForce.android.runvolution.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by akmalfadlurohman on 21/02/18.
 */

public class FCMService extends FirebaseMessagingService {
    private String LOG_TAG = FCMService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(LOG_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
