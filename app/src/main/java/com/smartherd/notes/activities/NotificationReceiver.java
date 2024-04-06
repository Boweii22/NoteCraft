package com.smartherd.notes.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Creating a notification channel (for Android 8.0 (API level 26) and above)
        String title = intent.getStringExtra("notification_title");
        String text = intent.getStringExtra("notification_text");
        long timestamp = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My notification channel";
            String description = "Channel for alarm notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Preparing the notification with a specific intent
        Intent notificationIntent = new Intent(context, CreateNoteActivity.class); // YourActivity is where you want to go when you click on the notification
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Log the details
        Log.d("NotificationReceiver", "Showing notification at: " + timestamp + " Title: " + title + " Text: " + text);


        // Building the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_channel_id") // Ensure this matches the channel ID used above
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Set a small icon for the notification
                .setContentTitle(intent.getStringExtra("notification_title")) // Use extras if you passed any, else set your own title
                .setContentText(intent.getStringExtra("notification_text")) // Use extras if you passed any, else set your own message
                .setContentIntent(pendingIntent) // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true); // Remove the notification once tapped

        // Triggering the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build()); // 0 is the ID of the notification
        }
    }
}
