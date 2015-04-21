package com.example.asmuniz.trojanow;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.app.PendingIntent;
import android.net.Uri;
import android.media.RingtoneManager;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;

import com.example.asmuniz.trojanow.obj.Post;
import com.example.asmuniz.trojanow.obj.User;

/**
 * Created by asmuniz on 4/18/15.
 */

public class PushReceiver extends BroadcastReceiver
{
    private static final String TAG = "push_rec";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //-----------------------------
        // Create a test notification
        //
        // (Use deprecated notification
        // API for demonstration purposes,
        // to avoid having to import
        // the Android Support Library)
        //-----------------------------
        int postId = intent.getIntExtra("id", 0);
        int userId = intent.getIntExtra("user_id", 0);
        String username = intent.getStringExtra("username");
        int feedId = intent.getIntExtra("feed_id", 1);
        String message = intent.getStringExtra("message");
        Log.d(TAG, "postId = " + postId);
        Log.d(TAG, "userId = " + userId);
        Log.d(TAG, "username = " + username);
        Log.d(TAG, "feedId = " + feedId);
        Log.d(TAG, "message = " + message);

        String notificationTitle = username;
        String notificationDesc = "";
        if (userId != User.getActiveUser().getId()) {
            notificationDesc = intent.getStringExtra("message");

            //-----------------------------
            // Get notification manager
            //-----------------------------

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent resultIntent = new Intent(context, PostListActivity.class);
            resultIntent.putExtra("feedId", feedId);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            0
                    );

            BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
            bigStyle.bigText(notificationDesc);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle(username)
                            .setContentText(notificationDesc)
                            .setAutoCancel(true)
                            .setContentIntent(resultPendingIntent)
                            .setStyle(bigStyle)
                            .setDefaults(Notification.DEFAULT_ALL);

            //-----------------------------
            // Show the notification
            //-----------------------------

            //mNotificationManager.notify(0, notification);

            // Sets an ID for the notification
            int mNotificationId = 001;
// Gets an instance of the NotificationManager service
            NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(context);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }
}
