package com.example.manavb.voicealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.Time;
import java.util.Calendar;

public class AlarmMaker {

    public static final String EXTRA_TIME = "alarm_time";
    public static final String EXTRA_RINGTONE = "alarm_ringtone_uri";
    public static final String EXTRA_VIBRATION = "alarm_vibration";

    public AlarmManager alarmMgr;
    public long startTime;

    private Context context;

    public AlarmMaker(Context context){
        this.context = context;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarmToRing(int requestCode, Time time, boolean[] days, String ringtoneUri, boolean vibration){ // Time received in a 24hr Time Object format here
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra(EXTRA_TIME, MainActivity.convertTo12HrTime(time.toString())); // passes time in 12hr format string
        alarmIntent.putExtra(EXTRA_RINGTONE, ringtoneUri); // ringtone uri as a string
        alarmIntent.putExtra(EXTRA_VIBRATION, vibration); // vibration as a boolean

        String[] tokens = time.toString().split(":", -3);
        int hours = Integer.parseInt(tokens[0]); // calculates hours from time strings tokens
        int mins = Integer.parseInt(tokens[1]); // calculates minutes

        for (int i = 0; i < days.length; i++){
            int alarmRequestCode = requestCode * 10 + i; // unique request code for every day the alarm is set for

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, mins);
            calendar.set(Calendar.SECOND, 0);
            long startTime;

            if (days[i]){
                calendar.set(Calendar.DAY_OF_WEEK, i + 1); // i = 1 is SUNDAY... i = 7 is SATURDAY
                startTime = calendar.getTimeInMillis(); // refresh startTime after resetting Calendar.Day_OF_WEEK
                if(calendar.before(Calendar.getInstance())){
                    startTime += AlarmManager.INTERVAL_DAY * 7;
                    Log.d("asdf", "startTime incremented. startTime is now: " + startTime);
                }

                Log.d("asdf", "[Set] Pending intents's requestCode: " + alarmRequestCode);
                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmRequestCode,
                        alarmIntent, 0);
                Log.d("asdf", "Start time right before setting alarm has changed to: " + startTime);
                //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY * 7, alarmPendingIntent);
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, alarmPendingIntent);

            }
        }

    }

    public void cancelAlarm(int requestCode, Time time, boolean[] days, String ringtoneUri, boolean vibration){
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra(EXTRA_TIME, MainActivity.convertTo12HrTime(time.toString())); // passes time in 12hr format string
        alarmIntent.putExtra(EXTRA_RINGTONE, ringtoneUri); // ringtone uri as a string
        alarmIntent.putExtra(EXTRA_VIBRATION, vibration); // vibration as a boolean

        for (int i = 0; i < days.length; i++) {
            int alarmRequestCode = requestCode * 10 + i; // unique request code for every day the alarm is set for
            if (days[i]) {
                // Cancelling the alarm
                PendingIntent.getBroadcast(context, alarmRequestCode, alarmIntent, 0).cancel();
                Log.d("asdf", "[Cancel] Pending intents's requestCode: " + alarmRequestCode);
            }
        }

    }





}
