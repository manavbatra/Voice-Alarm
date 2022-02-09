package com.example.manavb.voicealarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Time;
import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String ONBOARDING_COMPLETED = "App.FirstTimeUser.OnBoarding.Completed";

    public static final int ALARM_REQUEST = 1; // for return intent from FullScreenActivity
    public static final int ONBOARDING_REQUEST = 10;

    public static View mLayout;   // MainActivity's Layout as a view
    private RecyclerView mRecyclerView;
    private AlarmListAdapter mAdapter;
    private TextView mInfoTextView;
    private AdView mAdView;

    public final String[] DAYS_OF_WEEK = {"S", "M", "T", "W", "T", "F", "S"};
    public static DatabaseHelper databaseHelper;
    public static LinkedList<Alarm> alarm_list;
    public static int activeAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Check if we need to display our Onboarding Tutorial Activity (for first time user)
        if(!sharedPreferences.getBoolean(ONBOARDING_COMPLETED, false)) {
            // The user hasn't seen the OnboardingSupportFragment yet, so show it
            startActivityForResult(new Intent(getBaseContext(), WelcomeActivity.class), ONBOARDING_REQUEST);
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

        // Setting action toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        mLayout = findViewById(R.id.main_layout);

        /*
        Ask the user for this permission - FOR API LEVEL 29 (Android 10) and above, as they have
        more restrictions on starting activities from the Background.
        No need for API level < 29 (Android 10).
        TODO: Manage result code for result codes returned by dialog box. Show snackbar info accordingly.
         */

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            Thread askPermission = new Thread(new Runnable() {
                @Override
                public void run() {
                    AskPermissionsDialogFragment dialog = new AskPermissionsDialogFragment();
                    dialog.show(getSupportFragmentManager(), "permissions");
                }
            });
            askPermission.start();
        }

        /*
        INITIALIZING MEMBER VARIABLES, VIEWS AND ELEMENTS
        */
        mInfoTextView = findViewById(R.id.info_textView);

        /* Setting activity components to local variables */
        mRecyclerView = findViewById(R.id.recyclerview);

        /* initialize the alarmlist object */
        alarm_list = new LinkedList<>(); // Order: Name, Time, days, Ringtone, Vibration, Status

        /* Initializing alarm database helper */
        databaseHelper = new DatabaseHelper(this);
        activeAlarms = 0; // no. of active alarms, not the total count


        /* This if block only executes the first time a user opens the app, or when no alarms exist
        in the app anymore. This creates 3 placeholder alarms in the database, all disabled by default.
        Gives a starting point for the user.
         */


        if(databaseHelper.getAllData().getCount() == 0) {
            boolean inserted = databaseHelper.insertData("Alarm", "7:00:00", false,
                    new boolean[]{false, true, true, true, true, true, false},
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL).toString(), true);
            inserted = databaseHelper.insertData("Alarm", "7:30:00", false,
                    new boolean[]{false, true, true, true, true, true, false},
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL).toString(), true);
            inserted = databaseHelper.insertData("Alarm", "8:00:00", false,
                    new boolean[]{true, false, false, false, false, false, true},
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL).toString(), true);
        }

        /* EXTRACT ALARMS DATA FROM THE DATABASE TO POPULATE IN THE ALARM_LIST
         so it can be inflated to the RECYCLER VIEW. This method also assigns a value to integer activeAlarms */
        extractFromDataBase();  // private helper method, fills the alarmList


        /* If the database file contains any alarms, then set them to view in recycler */
        //Log.d("asdf", "------- printing value of alarmCount: " + alarmCount);

        if(databaseHelper.getAllData().getCount() != 0) {
            mAdapter = new AlarmListAdapter(this, alarm_list);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else
            mRecyclerView.setVisibility(View.INVISIBLE);

        /* PRINT ALARM LIST FOR TESTING */
//        for(Alarm a: alarm_list){
//            Log.d("asdf", "---" + "Alarm is:" + a.toString());
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // options selected handler for top-right corner options menu
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            Snackbar.make(mLayout, "Delete functionality will be available in the next update. ", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void startSecondActivity(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivityForResult(intent, ALARM_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = new Bundle(); // common bundle, regardless of the origin of intent
        String alarmTime = "", alarmName = "";
        //Ringtone ringtone = null;
        String ringtoneURIText = "";
        boolean alarmVibration = false, alarmStatus = true;
        boolean[] alarmDays = {false, false, false, false, false, false, false};
        int position = 0;

        // Custom made AlarmMaker class helps in managing (creating and cancelling) alarms
        /* Alarm scheduling of the current version has moved to make use of foregroundService.
            Therefore, no longer requires the AlarmMaker class.
         */
        AlarmMaker alarmMaker = new AlarmMaker(getBaseContext());

        /* The following 'if' block of code computes values for the common elements of the alarm,
        which are used by one of the subsequent if-else blocks of code after this one (depending on whether
        the it was a new alarm create request, or an alarm edit request)
         */
        if(requestCode == ALARM_REQUEST || requestCode == AlarmListAdapter.ALARM_EDIT_REQ){
            if(resultCode == RESULT_OK){
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });

                bundle = data.getExtras();

                assert (bundle != null);
                alarmName = bundle.getString(FullscreenActivity.EXTRA_NAME);
                alarmTime = bundle.getString(FullscreenActivity.EXTRA_TIME); // gets the time in 24hr format as a string
                alarmStatus = bundle.getBoolean(FullscreenActivity.EXTRA_STATUS, true);
                alarmDays = bundle.getBooleanArray(FullscreenActivity.EXTRA_DAYS);
                ringtoneURIText = bundle.getString(FullscreenActivity.EXTRA_RINGTONE);
                //ringtone = RingtoneManager.getRingtone(this, Uri.parse(ringtoneURIText));
                alarmVibration = bundle.getBoolean(FullscreenActivity.EXTRA_VIBRATION, false);
                position = bundle.getInt(FullscreenActivity.EXTRA_POSITION);
            }
        }

        /* The following code handles the New-Alarm request, and uses the computed values from the
        previous block of code.
         */
        if(requestCode == ALARM_REQUEST){
            if(resultCode == RESULT_OK){ // if SET button in pressed in FullScreenActivity

                assert alarmTime != null;
                Alarm newAlarm = new Alarm(alarmName, alarmTime, alarmStatus,
                        alarmDays, ringtoneURIText, alarmVibration); // newAlarm Object stores time in 24hr format string

//                // For testing purposes
//                Log.d(LOG_TAG, "Alarm Properties:- " + newAlarm.toString());

                /* Adding alarm values to the alarms_list, for showing it in the recycler*/
                alarm_list.addLast(newAlarm);

                // Update infoTextView Accordingly
                activeAlarms++;
                updateInfoTextView(activeAlarms);


                /* add the alarm to database
                 DATABASE, like newAlarmObject stores time in 24hr format string */
                boolean inserted = databaseHelper.insertData(newAlarm.getName(), newAlarm.getTime(), newAlarm.getStatus(),
                        newAlarm.getDays(), newAlarm.getRingtoneURIText(), newAlarm.getVibration());
                /* ^^^ From the database, an alarmList is populated on app onCreate, and the alarmList is
                inflated in the alarmListAdapter into a recyclerView. In the adapter, the 24hr time object is converted
                into a 12hr time string, and passed as a Sql Time Object into the AlarmMaker for disabling/enabling
                already set alarms.
                 */

                int alarmReqCode = databaseHelper.getAllData().getCount() - 1; /* this corresponds to the position of this
                    specific alarm in alarm_list, and is sent as a unique request code for PendingIntent in AlarmMaker */
                Log.d("hjkl", "Printing position right before setting alarm in Main Act: " + alarmReqCode);

                /* The following function sets the actual alarm in the background. */
                assert alarmDays != null;
                alarmMaker.setAlarmToRing(alarmReqCode, Time.valueOf(alarmTime),
                        alarmDays, ringtoneURIText, alarmVibration); // time is passed as a 24hr format Time Object

                /* Start the service */
//                Intent serviceIntent = new Intent(this, AlarmService.class);
//                stopService(serviceIntent);
//                startService(serviceIntent);

                // Update the recycler view
                mRecyclerView.getAdapter().notifyItemInserted(alarm_list.size());
                //mRecyclerView.smoothScrollToPosition(position - 1);

                if (inserted)
                    Toast.makeText(getBaseContext(), "Alarm scheduled.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), "Failed to add alarm. Please try again.", Toast.LENGTH_SHORT).show();

            }
            else if(resultCode == RESULT_CANCELED){ //if CANCEL button pressed in FullScreenActivity
                //Toast.makeText(getBaseContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
            }

        }

        if(requestCode == AlarmListAdapter.ALARM_EDIT_REQ){
            if(resultCode == RESULT_OK){
                //position = bundle.getInt(FullscreenActivity.EXTRA_POSITION);

                Log.d("asdf", "days received in main---: " + Arrays.toString(alarmDays));

                if (position != -1){
                    Alarm toBeRemoved = alarm_list.get(position);
                    /* If previous alarm's status was enabled, decrease activeAlarms
                     NOTE: DO THIS BEFORE REMOVING THE ALARM FROM THE ALARM_LIST */
                    if(toBeRemoved.getStatus())
                        activeAlarms--;
                    /* Cancel the deleted/edited alarm' pending intent (from schedule) */
                    assert alarmDays != null;
                    alarmMaker.cancelAlarm(position, Time.valueOf(toBeRemoved.getTime()), toBeRemoved.getDays(),
                            toBeRemoved.getRingtoneURIText(), toBeRemoved.getVibration());
                }

                Alarm newAlarm = new Alarm(alarmName, alarmTime, alarmStatus, alarmDays, ringtoneURIText, alarmVibration);
                alarm_list.set(position, newAlarm);

                /* Update infoTextView accordingly */
                activeAlarms++;
                updateInfoTextView(activeAlarms);

                // updating the value of that
                boolean updated = databaseHelper.updateData(String.valueOf(position + 1), newAlarm.getName(),
                        newAlarm.getTime(), newAlarm.getStatus(), newAlarm.getDays(),
                        newAlarm.getRingtoneURIText(), newAlarm.getVibration());

                assert alarmDays != null;
                alarmMaker.setAlarmToRing(position, Time.valueOf(alarmTime),
                        alarmDays, ringtoneURIText, alarmVibration); // time is passed as a 24hr format Time Object

                /* Start the service */
//                Intent serviceIntent = new Intent(this, AlarmService.class);
//                stopService(serviceIntent);
//                startService(serviceIntent);

                updateRecyclerView(alarm_list);
                mRecyclerView.smoothScrollToPosition(position);

                if (updated) { // if database didn't updated accordingly
                    Toast.makeText(getBaseContext(), "Alarm updated.", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "Alarm updated in database", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getBaseContext(), "Failed to update alarm.", Toast.LENGTH_SHORT).show();
                }

            } else{
                // Only for testing purposes
                //Log.d("asdf", "reached: Alarm edit request NOT SUCCESSFULL");
            }

        }


        if(requestCode == ONBOARDING_REQUEST){
            if(resultCode == RESULT_OK){
                // Do nothing.
                // just needs to be here in order to receive
            }
        }

        //------------------------------------------------------------------
        // update recycler view after the newly edited alarm(s)

        //updateRecyclerView(alarm_list);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* PRIVATE HELPER METHODS -------------------------------------------------------------------- */

    /*
    The following helper method calls getAllData to extract alarms data from the database
    and fill the local alarms_list linked list. Alarms list is used by the Recycler view to display
    the alarms set by the user.
     */
    public void extractFromDataBase(){
        // Querying the database is a cpu-intensive task, therefore done on a seperate thread

        // this thread runs fine ---
        new Thread(new Runnable() {
            @Override
            public void run() {

                Cursor result = databaseHelper.getAllData();

                // if database empty (i.e. no active/disabled alarms, although its never going to be empty), no need to extract
                if(result.getCount() == 0) {
                    mInfoTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            updateInfoTextView(0);
                        }
                    });

                    return;
                }

                // This code populates the local alarm_list object with relevant alarms data, extracted from database
                while(result.moveToNext()){
                    String name = result.getString(1);
                    String time = result.getString(2); // retrieves the time in a 24hr format string
                    String status = result.getString(3);
                    boolean stat = stringToBoolean(status);
                    String days = result.getString(4);
                    boolean[] daysArray = stringArrayToBooleanArray(days.substring(1, days.length() - 1).split(","));
                    String ringtoneURIText = result.getString(5);
                    String vibration = result.getString(6); boolean vib = stringToBoolean(vibration);

                    Alarm newAlarm = new Alarm(name, time, stat, daysArray, ringtoneURIText, vib);
                    alarm_list.addLast(newAlarm);

                    // If alarm status is true (enabled), increment active Alarms
                    if(stat) activeAlarms++;

                    /* //For testing purposes
                    Log.d("databasestuff", "status: " + status + " and stat: " + stat);
                    Log.d("databasestuff", "days: " + Arrays.toString(daysArray) + "\nringtoneUri " + ringtoneURIText +
                            "\n vibration: " + vibration + "\n");
                     */
                }

                mInfoTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Call helper method to update infoTextView
                        updateInfoTextView(activeAlarms);
                    }
                });
            }
        }).start();

    }

    private void updateRecyclerView(LinkedList<Alarm> list){
        mAdapter = new AlarmListAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void updateInfoTextView(int count){
        if(count == 0){
            mInfoTextView.setText(R.string.no_alarms_set_string);
        } else if(count == 1){
            mInfoTextView.setText(R.string.one_alarm_set_string);
        } else{
            mInfoTextView.setText(getString(R.string.active_alarms_set_string, count));
        }
    }

    public String[] booleanArrayToStringArray(boolean[] arr){
        String[] result = new String[arr.length];
        for(int i = 0; i < arr.length; i++){
            result[i] = DAYS_OF_WEEK[i];
        }
        return result;
    }

    public String booleanToString(boolean b){
        return b ? "true" : "false";
    }

    public boolean stringToBoolean(String str){
        return (str.equals("1"));
    }

    public boolean[] stringArrayToBooleanArray(String[] arr){
        boolean[] result = new boolean[arr.length];
        for(int i = 0; i < arr.length; i++){
            result[i] = (arr[i].equals("true")) || arr[i].equals(" true");
        }
        return result;
    }

    public static String convertTo12HrTime(String time){
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
        result += Integer.toString(hours) + ":" + minutes + " " + mode;

        return result;
    }


}
