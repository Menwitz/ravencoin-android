package com.ravencoin.tools.manager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.ravencoin.R;
import com.ravencoin.presenter.activities.WalletActivity;


public class RNotificationManager {
    public static final String TAG = RNotificationManager.class.getName();

    public static void sendNotification(Activity ctx, int icon, String title, String message, int mId) {
        if (ctx == null) {
            Log.e(TAG, "sendNotification: ctx is null");
            return;
        }

        Intent intent = new Intent(ctx.getApplicationContext(), WalletActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx.getApplicationContext(), 0, intent, 0);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.notification_channel_name);
            String description = ctx.getString(R.string.notificatin_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(mId), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx, String.valueOf(mId))
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent).setColor(ctx.getColor(R.color.restore_gradient_end))
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(ctx, ctx.getClass());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ctx);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx.getApplicationContext());
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(mId, mBuilder.build());

    }
}
