package com.example.manavb.voicealarm;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.sql.Time;
import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    //private static final String LOG_TAG = FullscreenActivity.class.getSimpleName();

    // DEFINING KEYS FOR RETURN INTENT KEY-VALUE PAIRS
    public static final String EXTRA_NAME = "alarm_name";
    public static final String EXTRA_TIME = "alarm_time";
    public static final String EXTRA_STATUS = "alarm_status";
    public static final String EXTRA_DAYS = "alarm_days";
    public static final String EXTRA_VIBRATION = "alarm_vibration";
    public static final String EXTRA_RINGTONE = "alarm_ringtone";
    public static final String EXTRA_POSITION = "com.example.manavb.voicealarm.extra.POSITION";

    // Declaring important components of the activity
    private TimePicker mTimePicker;
    private EditText mAlarmNameEditText;
    private ToggleButton[] mToggleButtons;
    private boolean[] mDays;
    private TextView mAlarmSoundName;
    private Ringtone mRingtone;
    private String chosenRingtoneName;
    private Uri uri; // needed to remember previous selected ringtone when ringtone picker activity is opened
    private Switch mVibrationSwitch;
    private boolean vibration;
    private int position; // position of the alarm to be removed from the alarmList (from alarmList)
    private AdView mAdView;

    // Other small variables
    //public static AlphaAnimation buttonClickAnimation = new AlphaAnimation(1F, 0.3F); // clickable animation for button's onclick
    private static int toastClickNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* Initializing the Google AdMob SDK */
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        toastClickNumber = 0;

        // Setting IDs to all activity components -------------------------------
        mTimePicker = findViewById(R.id.timePicker1);
        mAlarmNameEditText = findViewById(R.id.alarm_name_editText);
        mToggleButtons = new ToggleButton[7];
        mToggleButtons[0] = findViewById(R.id.toggleButton1);
        mToggleButtons[1] = findViewById(R.id.toggleButton2);
        mToggleButtons[2] = findViewById(R.id.toggleButton3);
        mToggleButtons[3] = findViewById(R.id.toggleButton4);
        mToggleButtons[4] = findViewById(R.id.toggleButton5);
        mToggleButtons[5] = findViewById(R.id.toggleButton6);
        mToggleButtons[6] = findViewById(R.id.toggleButton7);
        mDays = new boolean[]{false, false, false, false, false, false, false};
        mAlarmSoundName = findViewById(R.id.alarm_sound_name);
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL); // default ringtone, if unspecified
        mVibrationSwitch = findViewById(R.id.alarm_vibration_switch);
        chosenRingtoneName = "";

        /* by default, sets the ringtone to system default */
        mAlarmSoundName.setText(RingtoneManager.getRingtone(this, uri).getTitle(this));

        position = -1; // unless its an alarm Edit request, the default position is -1

        /* By default, check the currentDay's toggle button selected */
        if(getIntent().getExtras() == null){// only do this if its a new alarm request, not for editAlarm
            Calendar calendar = Calendar.getInstance();
            int activeDay = calendar.get(Calendar.DAY_OF_WEEK);
            mToggleButtons[activeDay - 1].setChecked(true); // toggle Buttons index starts = 0, Calendar.Sunday = 1
            mToggleButtons[activeDay - 1].setTextColor(getResources().getColor(R.color.colorSecondary));
        }


        /* If it's a request to edit an existing alarm, then the intent is coming from the
        adapter's onClick method. Pre-fill the alarm fields from the intent's extra fields.
         */
        if(getIntent() != null){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            try{
                if (bundle != null){
                    System.out.println("----------- FULL SCREEN ACTIVITY; BUNDLE IS NOT NULL");
                    /* preset alarm name */
                    mAlarmNameEditText.setText(bundle.getString(EXTRA_NAME));

                    /* preset alarm time */
                    String presetTime = bundle.getString(EXTRA_TIME);
                    assert presetTime != null;
                    String[] tokens = presetTime.split(":");
                    int hours = Integer.parseInt(tokens[0]); // calculates hours from time string
                    int mins = Integer.parseInt(tokens[1].split(" ")[0]); // calculates minutes
                    mTimePicker.setHour(hours);
                    mTimePicker.setMinute(mins);

                    /* preset days */
                    boolean[] presetDays = bundle.getBooleanArray(EXTRA_DAYS);
                    if (presetDays != null) {
                        for (int i = 0; i < presetDays.length; i++){
                            mToggleButtons[i].setChecked(presetDays[i]);
                            if(presetDays[i])
                                mToggleButtons[i].setTextColor(getResources().getColor(R.color.colorSecondary));
                        }
                    }

                    /* preset vibration */
                    boolean presetVibration = bundle.getBoolean(EXTRA_VIBRATION);
                    mVibrationSwitch.setChecked(presetVibration);

                    /* preset ringtone */
                    uri = Uri.parse(bundle.getString(EXTRA_RINGTONE));
                    Ringtone rg = RingtoneManager.getRingtone(this, uri);
                    mAlarmSoundName.setText(rg.getTitle(this));

                    position = bundle.getInt(EXTRA_POSITION);
                }

            } catch (NullPointerException e){
                e.printStackTrace();
            }

        }

//        Log.d("asdf", "position value in onCreate of FullscreenActivity (outside): " + position);


    }

    /* CANCEL BUTTON PRESSED, GO BACK TO MAIN WITHOUT SETTING AN ALARM */
    public void goBackToMain(View view) {
        //view.startAnimation(buttonClickAnimation);
        Intent returnIntent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /* SET BUTTON PRESSED, GO BACK TO MAIN, SET AN ALARM */
    public void setAlarm(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                /* INSTANTIATING NEW ALARM AS SOON AS THE USER CLICKS THE SET ALARM BUTTON */
                Alarm newAlarm = new Alarm("MY ALARM");

        /* DECLARING A BUNDLE THAT STORES ALL INFORMATION ABOUT THE ALARM AND
        IS PASSED AS EXTRAS THROUGH THE INTENT TO MAIN ACTIVITY */
                Bundle bundle = new Bundle();

                // Setting alarm NAME, extracted from EditText
                String alarmName = mAlarmNameEditText.getText().toString();
                if (!alarmName.isEmpty())
                    newAlarm.setName(alarmName);
                else
                    newAlarm.setName("My alarm");   // default name, if the user doesn't provide one
                bundle.putString(EXTRA_NAME, newAlarm.getName());

                // Setting alarm TIME, extracted from TimePicker
                Time alarmTime = getTimeFromPicker(); // gets the time from Picker in 24hr format "hh:mm:ss"
                String timeString = alarmTime.toString(); // puts the in 24hr format as a string in the bundle
                newAlarm.setTime(timeString);
                bundle.putString(EXTRA_TIME, timeString);

                // Setting active DAYS, extracted from toggle button
                for (int i = 0; i < 7; i++) {
                    mDays[i] = mToggleButtons[i].isChecked();
                }
                newAlarm.setDays(mDays);
                bundle.putBooleanArray(EXTRA_DAYS, mDays);

                // Alarm sound picker
                //newAlarm.setRingtoneURI(this.mRingtone); // DOES NOT WORK
                //newAlarm.setRingtoneURI(uri.toString());
                bundle.putString(EXTRA_RINGTONE, uri.toString());   // sends the ringtone URI in the bundle

                // Setting Vibration (only true/false options in the early stages
                vibration = mVibrationSwitch.isChecked();
                newAlarm.setVibration(vibration); // helper method below
                bundle.putBoolean(EXTRA_VIBRATION, vibration);

                // Setting alarm status (default: ON)
                newAlarm.setStatus(true);
                bundle.putBoolean(EXTRA_STATUS, true);

                // Setting alarm position (default value of position = -1)
                bundle.putInt(EXTRA_POSITION, position);
                Log.d("asdf", "position returned in FullScreen Activity: " + position);

                // DEFINING RETURN_INTENT, AND PASSING THE BUNDLE THROUGH IT--------------------------------
                Intent returnIntent = new Intent();
                returnIntent.putExtras(bundle);
                setResult(RESULT_OK, returnIntent);

                finish();
            }
        }).start();

    }

    @TargetApi(23)
    private Time getTimeFromPicker(){
        int hour = mTimePicker.getHour();
        int minute = mTimePicker.getMinute();
        return new Time(hour, minute, 0);
    }

    public void setAlarmSound(View view) {
        //view.startAnimation(buttonClickAnimation);

        /* Starts a new popup ringtone picker activity */
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);

        startActivityForResult(intent, 5);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent){

        /* RINGTONE PICKER AND HANDLER (NEW POPUP ACTIVITY) */
        if(resultCode == Activity.RESULT_OK && requestCode == 5){
            uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            mRingtone = RingtoneManager.getRingtone(this, uri);
            if(uri != null) {
                //Log.d("ringtone name", "Ringtone name is: " + ringtone.getTitle(this));
                this.chosenRingtoneName = mRingtone.getTitle(this);
                mAlarmSoundName.setText(this.chosenRingtoneName);
            }
            else
                this.chosenRingtoneName = "";
        }

    }

    public void setAlarmVibration(View view) {
        //VibrationEffect.createOneShot(4000, 200);
        vibration = (mVibrationSwitch.isChecked());
    }

    public void setAlarmSnooze(View view) {
        Switch snoozeSwitch = findViewById(R.id.alarm_snooze_switch);

        toastClickNumber++;

        // Various funny toasts to discourage the snooze option
        if(toastClickNumber == 1)
            Toast.makeText(this, "If you really want to wake up, don't even go there...", Toast.LENGTH_LONG).show();
        else if(toastClickNumber == 2)
            Toast.makeText(this, "Haven't you heard? You snooze, you lose!", Toast.LENGTH_SHORT).show();
        else if(toastClickNumber == 3)
            Toast.makeText(this, "Trust me. I really want you to wake up on time :)", Toast.LENGTH_LONG).show();
        else if(toastClickNumber == 4)
            Toast.makeText(this, "Try some sound options, or perhaps a different vibration.", Toast.LENGTH_LONG).show();
        else if(toastClickNumber == 5)
            Toast.makeText(this, "Wow! You seem quite determined. But sorry, no can-do!", Toast.LENGTH_LONG).show();
        else if(toastClickNumber >= 6)
            Toast.makeText(this, "I apologize, but I can't let you do this.", Toast.LENGTH_SHORT).show();

        // making the switch go off if the user tries to turn it on
        if(snoozeSwitch.isChecked())
            snoozeSwitch.setChecked(false);
        else
            snoozeSwitch.setChecked(false);

    }


    public void toggleButtonClick(View view) {
        if(((ToggleButton)view).getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorSecondary)){
            ((ToggleButton) view).setTextColor(ContextCompat.getColor(this, R.color.colorSecondary));
        } else
            ((ToggleButton) view).setTextColor(ContextCompat.getColor(this, R.color.activityBg));
    }
}
