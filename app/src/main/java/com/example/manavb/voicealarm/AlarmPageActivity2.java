package com.example.manavb.voicealarm;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import my.app.manavb.voicealarm.AlarmService;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AlarmPageActivity2 extends AppCompatActivity {

    /* Backend Variables */
    private boolean mVisible;
    final String TAG = "abcd";
    public final static int SPEECH_RECOGNITION_REQUEST = 5;

    public static PowerManager powerManager;
    public static PowerManager.WakeLock wakeLock;

    private Uri alarmUri = null;
    private String alarmTime12hrFormat;
    private boolean alarmVibration;
    private TextView mCurrentTimeView;
    private TextView mSpeechString; // the string that the user has to speak
    public SpeechRecognizer recognizer;
    private TextView mSpeechOutput; // the actual words spoken by the user
    private MediaPlayer mediaPlayer;
    private View mLayoutView;
    private Vibrator mVibrator;

    /* Front End Variables */
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds. */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI. */
    private static final int AUTO_HIDE_DELAY_MILLIS = 300;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar. */
    private static final int UI_ANIMATION_DELAY = 100;

    private View mContentView; // USED FOR MANIPULATING THE FULLSCREEN VIEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_page2);

        /* Front end */
        mVisible = true;
        mContentView = findViewById(R.id.textView);

        /* Back end */
        mLayoutView = findViewById(R.id.layout_alarm_page);

        /* initializing memeber variables */
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mCurrentTimeView = findViewById(R.id.current_time_textView);
        mSpeechString = findViewById(R.id.speech_string);
        mSpeechOutput = findViewById(R.id.speech_output_textView);
        mediaPlayer = new MediaPlayer();
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mSpeechString.setText(getRandomDismissString()); // gets a random quote (string) to show every morning

        // Getting the alarm Uri and alarmTime from incoming intent
        if (getIntent() != null){
            Intent intent = getIntent();
            if (intent.getStringExtra(AlarmService.EXTRA_RINGTONE) != null)
                alarmUri = Uri.parse(intent.getStringExtra(AlarmService.EXTRA_RINGTONE));
            alarmTime12hrFormat = intent.getStringExtra(AlarmService.EXTRA_TIME);
            alarmVibration = intent.getBooleanExtra(AlarmService.EXTRA_VIBRATION, false);
        }

        /* Play the ringtone */
        if (alarmUri == null)
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

        try {
            mediaPlayer.setDataSource(this, alarmUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Since preparing the media player is an intensive task, it executes on a separate thread.
        Uses it's own Async method for that.
         */
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(5.0f, 5.0f);
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            }
        });

        /* Vibrate the device */
        if (alarmVibration)
            vibrateDevice();

        /* Setting Current Time View */
        mCurrentTimeView.setText(alarmTime12hrFormat);

    }

    @Override
    protected void onStart() {
//        Log.d(TAG, "onStart method");
        super.onStart();

        final Window window = getWindow();
        window.makeActive();
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            /*| WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY*/);

        acquireScreenCpuWakeLock(this);
    }

    @Override
    protected void onResume() {
//        Log.d(TAG, "onResume method");
        super.onResume();

        /* front end */
        hide();

        /* back end */

        // play the audio
        mediaPlayer.start();
        if (alarmVibration)
            vibrateDevice();

        // hide the navigation bar
//        final View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    @Override
    protected void onPause() {
//        Log.d(TAG, "onPause method");
        super.onPause();
        mLayoutView.setVisibility(View.VISIBLE);
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    protected void onStop() {
        mVibrator.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        Log.d(TAG, "onDestroy method");

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null; // the proper way to release the media player
        mVibrator.cancel();

        Intent service = new Intent(this, AlarmService.class);
        stopService(service);

        super.onDestroy();
        wakeLock.release(); // causing wakeLock under-locked exception
    }

    public static void acquireScreenCpuWakeLock(Context context){
        if(wakeLock != null){
            return;
        }
        powerManager = (PowerManager)context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyApp::MyWakeLockTag");
        wakeLock.acquire(120000);
    }

    public void recordSpeech(View view) { // micButton's onclick method
        startSpeechRecognizer();
    }

    public void dismissAlarm() {
        finishAndRemoveTask();
        //finishAffinity();
    }

    private String getRandomDismissString() {
        List<String> list = new ArrayList<>();
        list.add("the quick brown fox jumps over the lazy dog");
        list.add("an apple a day keeps the doctor away");
        list.add("an early morning walk is a blessing for the whole day");
        list.add("morning comes whether you set the alarm or not");
        list.add("lose an hour in the morning and you will spend all day looking for it");
        list.add("opportunities don't happen. You create them");
        list.add("all progress takes place outside the comfort zone");
        list.add("some people dream of success while others work for it");
        list.add("your mindset is everything when it comes to being successful");
        list.add("every new day is a chance to do something great in life");

        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public void startSpeechRecognizer(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mSpeechString.getText().toString() + "\n");
        startActivityForResult(intent, SPEECH_RECOGNITION_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_RECOGNITION_REQUEST){
            if (resultCode == RESULT_OK){
                if (data != null){
                    List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    /* results is a list of all the possible results (possible strings from user's recording).
                        Therefore we chose the most confident result of those. 99% of time it was the first
                        item of the list. But, the below code gets the most accurate result based on
                        the confidence scores provided by the RecognizerIntent API.
                     */
                    float[] scores = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    int mostConfidentPos = 0;
                    float mostConfidentScore = 0.0f;
                    if(scores != null){
                        for(int i = 0; i < scores.length; i++){
                            if(scores[i] > mostConfidentScore){
                                mostConfidentScore = scores[i];
                                mostConfidentPos = i;
                            }
                        }
                    }

                    /* the above code gives a proper string */
                    String answer = "";
                    if(results != null)
                        answer = results.get(mostConfidentPos).trim();
                    answer = answer.trim();


                    /* remove any periods from the string */
                    String question = mSpeechString.getText().toString();
                    if (question.contains("."))
                        question = question.replace(". ", " ");

                    /* if the voice recorded string matches the required string, then dismissAlarm() */
                    if(answer.compareToIgnoreCase(question) == 0){
                        dismissAlarm();
                    }
                    else
                        Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();

                }
            } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH)
                System.out.println("result no match code");
        }
    }

    public void vibrateDevice() { // vibrate for 3 mins
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(60000, 255));
        } else {
            mVibrator.vibrate(60000);
        }
        System.out.println("The device is vibrating...,,,,,,,");
    }






    /* ------------------------------------------------------------------------------------------ */
    /* ----------------------------- FULL SCREEN VIEW UI FUNCTIONALITY --------------------------*/
    /* ------------------------------------------------------------------------------------------ */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
        hide();
    }

    // not needed
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    // needed (for hiding)
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    // -----------------------------------------------------------------
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}