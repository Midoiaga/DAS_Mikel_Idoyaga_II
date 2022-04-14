package com.example.das_mikel_idoyaga_ii;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {
    }
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d("noti","mensaje");
        }
        if (remoteMessage.getNotification() != null) {
            Log.d("noti","estoy");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager elmanager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel canalservicio = new NotificationChannel("IdCanal", "NombreCanal",NotificationManager.IMPORTANCE_LOW);
                elmanager.createNotificationChannel(canalservicio);
                Notification.Builder builder = new Notification.Builder(getApplicationContext(), "IdCanal").setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true);
                Notification notification = builder.build();
                elmanager.notify(1,notification);
            }
        }

    }
}