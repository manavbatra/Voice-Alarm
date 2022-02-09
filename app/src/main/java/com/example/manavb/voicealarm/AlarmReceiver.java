package com.example.manavb.voicealarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import my.app.manavb.voicealarm.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {
    private String alarmUri;
    private String alarmTime12HrFormat;
    private boolean vibration;

    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    //private static final String ACTION_CUSTOM_BROADCAST = "ACTION_CUSTOM_BROADCAST_ALARM";
    public NotificationManager mNotifyManager;

    public static PowerManager powerManager;
    public static PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("asdfasdf", "BroadcastReceiver (alarmReceiver) activity reached");

        powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyApp::MyWakeLockTag");
        wakeLock.acquire(120000);

        //mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //createNotificationChannel(context);
        //mNotifyManager.notify(0, getNotificationBuilder(context).build());

        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtras(intent);
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.stopService(serviceIntent);
        context.startService(serviceIntent);

//        Intent alarmIntent = new Intent(context, AlarmPageActivity2.class);
//        if (intent != null){
//            alarmIntent.putExtras(intent);
//            System.out.println("Reaches AlarmReceiver code and gets intent.");
//        }
//
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        context.startActivity(alarmIntent);

        Log.d("asdf", "broadcast receiver ended");

    }

    public NotificationCompat.Builder getNotificationBuilder(Context context){
        Intent notifIntent = new Intent(context, AlarmPageActivity2.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, notifIntent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_new_launcher_icon_foreground)
                        .setContentTitle("Incoming Alarm")
                        .setContentText("Test alarm")
                        .setContentIntent(pi)
                        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                        //.setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setAutoCancel(false)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setChannelId(PRIMARY_CHANNEL_ID);
        //.setDefaults(NotificationCompat.DEFAULT_ALL);


        return builder;
    }

    public void createNotificationChannel(Context context){
        //mNotifyManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Alarm Receiver notification", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription("Notifications from Alarm Receiver");

            mNotifyManager.createNotificationChannel(notificationChannel);

        }
    }

}
