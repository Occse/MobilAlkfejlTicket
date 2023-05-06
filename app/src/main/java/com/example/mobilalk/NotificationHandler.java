package com.example.mobilalk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "Ticket_notification_channel";
    private static final int NOTIFICATION_ID=0;
    private NotificationManager mNotif;
    private Context context;

    public NotificationHandler(Context context) {
        this.context = context;
        this.mNotif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O)
            return;
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                "Ticket shop notification",
                NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("Notifications from Ticket shop application.");
        this.mNotif.createNotificationChannel(notificationChannel);
    }

    public void send(String message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle("Concert Ticket Shop")
                .setContentText(message)
                .setSmallIcon(R.drawable.cart);
        this.mNotif.notify(NOTIFICATION_ID, builder.build());
    }
}
