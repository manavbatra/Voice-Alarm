package my.app.manavb.voicealarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.manavb.voicealarm.AlarmPageActivity2;
import com.example.manavb.voicealarm.MainActivity;
import com.example.manavb.voicealarm.R;

public class AlarmService extends Service {

    public static final String EXTRA_TIME = "alarm_time";
    public static final String EXTRA_RINGTONE = "alarm_ringtone_uri";
    public static final String EXTRA_VIBRATION = "alarm_vibration";
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 9;

    public NotificationManager mNotifyManager;
    public AlarmManager alarmMgr;
    //List<Alarm> list = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        /* copy the alarm list from MainActivity to a local object here */
//        list.addAll(MainActivity.alarm_list);
//        Log.d("hjkl", "**************** list in alarmService is\n: " + MainActivity.alarm_list);

        /* IF NO ENABLED ALARMS EXIST, STOPSELF() */
//        boolean exists = false;
//        for(int i = 0; i < list.size() && !exists; i++){
//            exists = list.get(i).getStatus();
//        }
//        if (!exists)
//            stopSelf();

        alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotificationBuilder().build());


        // Set the broadcast for resetting this Alarm Service on the start of every week.
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0,
//                        new Intent(getBaseContext(), BootReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
//                Calendar c1 = Calendar.getInstance();
//                c1.setTimeInMillis(System.currentTimeMillis());
//                c1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//                c1.set(Calendar.HOUR_OF_DAY, 12);
//                c1.set(Calendar.MINUTE, 0);
//                c1.set(Calendar.SECOND, 0);
//                long time = c1.getTimeInMillis();
//                while(time < Calendar.getInstance().getTimeInMillis()){
//                    time += AlarmManager.INTERVAL_DAY * 7;
//                }
//                alarmMgr.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY * 7, pi);
//            }
//        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("asdfasdf", "---service onStartCmd");
        Toast.makeText(this, "service started (onStartCmd)", Toast.LENGTH_SHORT).show();

        Intent alarmIntent = new Intent(this, AlarmPageActivity2.class);
        alarmIntent.putExtras(intent);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        getApplicationContext().startActivity(alarmIntent);

//        for(int i = 0; i < list.size(); i++){
//            Alarm alarm = list.get(i);
//            /*
//             Proceed if it's an enabled alarm
//             */
//            if (alarm.getStatus()){
//                Intent alarmIntent = new Intent(this, AlarmPageActivity2.class);
//                alarmIntent.putExtra(EXTRA_TIME, convertTo12HrTime(alarm.getTime())); // passes time in 12hr format string
//                alarmIntent.putExtra(EXTRA_RINGTONE, alarm.getRingtoneURIText()); // ringtone uri as a string
//                alarmIntent.putExtra(EXTRA_VIBRATION, alarm.getVibration()); // vibration as a boolean
//                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//
//                /* Computing the hours:mins from time string */
//                String[] tokens = alarm.getTime().toString().split(":", -3);
//                int hours = Integer.parseInt(tokens[0]); // calculates hours from time strings tokens
//                int mins = Integer.parseInt(tokens[1]); // calculates minutes
//
//                /* Creating a calendar object from the above extracted time */
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                calendar.set(Calendar.HOUR_OF_DAY, hours);
//                calendar.set(Calendar.MINUTE, mins);
//                calendar.set(Calendar.SECOND, 0);
//
//                /* Scheduling the ALARMS */
//                for (int j = 0; j < alarm.getDays().length; j++){
//                    if (alarm.getDays()[j]){
//                        int alarmRequestCode = i * 10 + j; // unique request code for every day the alarm is set for
//                        long startTime;
//
//                        calendar.set(Calendar.DAY_OF_WEEK, j + 1); // i = 1 is SUNDAY... i = 7 is SATURDAY
//                        startTime = calendar.getTimeInMillis(); // refresh startTime after resetting Calendar.Day_OF_WEEK
//                        /*
//                        TODO: CHANGE THIS IF STATEMENT TO WHILE STATEMENT IF PREVIOUSLY SET ALARMS
//                         DON'T RING IN THE FUTURE.
//                         */
//                        if(calendar.before(Calendar.getInstance())){
////                            System.out.println("(Calendar) Current startTime: " + calendar.getTime() + " set time was before calendar instance: " +
////                                    Calendar.getInstance().getTime());
//                            startTime += AlarmManager.INTERVAL_DAY * 7;
//                            //Log.d("hjkl", "startTime incremented. startTime is now: " + startTime);
//                        }
//
//                        Log.d("hjkl", "[Set] Pending intents's requestCode: " + alarmRequestCode);
//                        PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, alarmRequestCode,
//                                alarmIntent, 0);
//                        Log.d("hjkl", "Start time right before setting alarm has changed to: " + startTime);
//                        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY * 7, alarmPendingIntent);
//                        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, alarmPendingIntent);
//
//                    }
//                }
//            }
//        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done (onDestroy)", Toast.LENGTH_SHORT).show();

        Log.d("asdf", "---service stopped");

        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public NotificationCompat.Builder getNotificationBuilder(){
        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, NOTIFICATION_ID + 7, notifIntent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                        //.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                        //.setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_new_launcher_icon_foreground))
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle("Alarms scheduled")
                        .setContentText("Tap to view")
                        .setContentIntent(pi)
                        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                        //.setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setAutoCancel(false)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setChannelId(PRIMARY_CHANNEL_ID);
        //.setDefaults(NotificationCompat.DEFAULT_ALL);

        return builder;
    }

    public void createNotificationChannel(){
        mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Alarm service notification", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription("Notifications from Alarm Service");

            mNotifyManager.createNotificationChannel(notificationChannel);

        }
    }

    /* Private helper method */
    private String convertTo12HrTime(String time){
        String result = "";
        String mode = "a.m.";
        String[] tokens = time.split(":");
        int hours = Integer.parseInt(tokens[0]);
        String minutes = tokens[1];
        if(hours > 12 && hours < 24){
            hours = hours - 12;
            mode = "p.m.";
        } else if(hours == 0)
            hours = 12;
        else if(hours == 12)
            mode = "p.m.";
        result += (hours) + ":" + minutes + " " + mode;

        return result;
    }
}
