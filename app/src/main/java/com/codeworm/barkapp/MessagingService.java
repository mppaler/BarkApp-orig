package com.codeworm.barkapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mariah on 10/03/2018.
 */

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size() > 0){
            Log.d("HI", "Message data payload: " + remoteMessage.getData());
            try{
                JSONObject data = new JSONObject(remoteMessage.getData());
                String jsonMessage = data.getString("extra_information");

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        if(remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            showNotifications(title, message);
        }

    }

    public void showNotifications(String title, String message){
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),0);
        Notification notification = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_barkapp_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}
