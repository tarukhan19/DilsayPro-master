package com.dbvertex.dilsayproject.Fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final int BADGE_ICON_SMALL = 1;

    int MESSAGE_NOTIFICATION_ID = 0;
    Intent intent;
    String message, pushType, postid;
    private SharedPreferences sp;
    SessionManager sessionManager;
    String countS = "0";
    int count = 0, i = 0;
    NotificationCompat.Builder mBuilder;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "getData:push " + remoteMessage.getData());
        pushType = remoteMessage.getData().get("key");
        message = remoteMessage.getData().get("body").replace("\"", "");
        sessionManager = new SessionManager(getApplicationContext());


        sp = getSharedPreferences("photo", MODE_PRIVATE);

            sendNotification();

// {content-available=1, notification_title=Notification, badge_count=1, key=Notification, body=Your Account Inactivated From H1bq, badge=1}

    }


    @SuppressLint("WrongConstant")
    public void sendNotification() {

        String id = "my_channel_01";
        Context context = getBaseContext();

        MESSAGE_NOTIFICATION_ID = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pIntent = PendingIntent.getActivity(context, MESSAGE_NOTIFICATION_ID, intent, MESSAGE_NOTIFICATION_ID);

        mBuilder = new NotificationCompat.Builder(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }


        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("H1BQ")
                .setContentText(message)
                .setContentIntent(pIntent)
                .setBadgeIconType(BADGE_ICON_SMALL)
                .setNumber(Integer.parseInt(countS))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setChannelId(id);


        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
                mChannel.setShowBadge(true);

            }
        }
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }


    public void cancelNotification() {
        Context context = getBaseContext();
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }


    public void clearNotification() {


    }
}