package com.example.manavb.voicealarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.sql.Time;
import java.util.LinkedList;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>{

    public static final int ALARM_EDIT_REQ = 3;

    private LinkedList<Alarm> mAlarmList;
    private LayoutInflater mInflater;

    private Context context;


    public AlarmListAdapter(Context context, LinkedList<Alarm> alarmList){
        mInflater = LayoutInflater.from(context);
        this.mAlarmList = alarmList;
        this.context = context;
    }

    /* Inner HOLDER class -------------------------------------------- */

    class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView alarmTimeView;
        public final LinearLayout alarmDaysView;
        public final TextView alarmNameView;
        public final Switch alarmStatusView;
        public final TextView sunView;
        public final TextView monView;
        public final TextView tueView;
        public final TextView wedView;
        public final TextView thuView;
        public final TextView friView;
        public final TextView satView;
        public LinearLayout layout;

        final AlarmListAdapter mAdapter;

        public AlarmViewHolder(View itemView, AlarmListAdapter adapter){
            super(itemView);

            alarmNameView = itemView.findViewById(R.id.alarm_name_text_view);
            alarmTimeView = itemView.findViewById(R.id.alarm_time_text_view);
            alarmDaysView = itemView.findViewById(R.id.days_linear_layout);
            alarmStatusView = itemView.findViewById(R.id.alarm_enabler_switch);
            sunView = itemView.findViewById(R.id.sunday_view);
            monView = itemView.findViewById(R.id.monday_view);
            tueView = itemView.findViewById(R.id.tuesday_view);
            wedView = itemView.findViewById(R.id.wednesday_view);
            thuView = itemView.findViewById(R.id.thursday_view);
            friView = itemView.findViewById(R.id.friday_view);
            satView = itemView.findViewById(R.id.saturday_view);

            layout = itemView.findViewById(R.id.alarm_item_id);

            this.mAdapter = adapter;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            Alarm alarm = mAlarmList.get(mPosition);

            Intent intent = new Intent(context, FullscreenActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(FullscreenActivity.EXTRA_NAME, alarm.getName());
            bundle.putString(FullscreenActivity.EXTRA_TIME, alarm.getTime()); // time passed is in the 24hr format string
            bundle.putBooleanArray(FullscreenActivity.EXTRA_DAYS, alarm.getDays());
            bundle.putString(FullscreenActivity.EXTRA_RINGTONE, alarm.getRingtoneURIText());
            bundle.putBoolean(FullscreenActivity.EXTRA_VIBRATION, alarm.getVibration());
            bundle.putInt(FullscreenActivity.EXTRA_POSITION, mPosition);
            intent.putExtras(bundle);

            ((MainActivity) context).startActivityForResult(intent, ALARM_EDIT_REQ);
//            mAdapter.notifyDataSetChanged();
//            mAdapter.notifyItemChanged(mPosition);

            Log.d("asdf", "reached after notify data set changed ");
        }


    }

    @NonNull
    @Override
    public AlarmListAdapter.AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.alarmlist_item, parent, false);
        return new AlarmViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmListAdapter.AlarmViewHolder holder, final int position) {
        final int actPosition = holder.getAdapterPosition(); // don't use the position from the arguments
        final Alarm mAlarm = mAlarmList.get(actPosition);

        String mName = mAlarm.getName();
        String mTime = "";
        mTime = mAlarm.getTime();
        boolean mStatus = mAlarm.getStatus();
        boolean[] mDays = mAlarm.getDays();
        //Log.d("asdf", "Alarm days for pos: " + actPosition + " in adapter: " + Arrays.toString(mDays));

        /* UPDATING THE VIEWS ELEMENTS (NAME, TIMES, DAYS,...) */
        holder.alarmNameView.setText(mName);
        holder.alarmTimeView.setText(convertTo12HrTime(mTime)); // time has to be converted from 24hr format string --> 12hr format string
        holder.alarmStatusView.setChecked(mStatus);
        for(int i = 0; i < mDays.length; i++){
            TextView temp = new TextView(context);
            if(mDays[i]){
                switch (i){
                    case 0:
                        temp = holder.sunView;
                        break;
                    case 1:
                        temp = holder.monView;
                        break;
                    case 2:
                        temp = holder.tueView;
                        break;
                    case 3:
                        temp = holder.wedView;
                        break;
                    case 4:
                        temp = holder.thuView;
                        break;
                    case 5:
                        temp = holder.friView;
                        break;
                    case 6:
                        temp = holder.satView;
                }
                //Log.d("hjkl", "temp for pos: " + actPosition + " was : " + temp.getText().toString());
                //if(temp != null) {
                (temp).setTypeface(Typeface.DEFAULT_BOLD);
                (temp).setTextSize(13);
                temp.setTextColor(ContextCompat.getColor(context, R.color.textColor)); // enabled the dayView
                //}
            } else{
                (temp).setTypeface(Typeface.DEFAULT);
                (temp).setTextSize(12);
                temp.setTextColor(ContextCompat.getColor(context, R.color.disabledTextColor)); // disable the dayView
            }
        }


        if (mStatus)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.enabledAlarmColor));
        else
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.disabledAlarmColor));

        final AlarmMaker alarmMaker = new AlarmMaker(context);

        holder.alarmStatusView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAlarmList.get(actPosition).setStatus(b);
                Log.d("asdf", "This code is getting called in Adapter for position: " + position);
                Log.d("asdf", "This code is getting called in Adapter for getAdapterPosition: " + actPosition);

                /* update database status value */
                //AlarmMaker alarmMaker = new AlarmMaker(context);
                if (b) {
                    mAlarmList.get(actPosition).setStatus(true);
                    MainActivity.databaseHelper.updateData(String.valueOf(actPosition + 1), mAlarm.getName(),
                            mAlarm.getTime(), mAlarm.getStatus(), mAlarm.getDays(),
                            mAlarm.getRingtoneURIText(), mAlarm.getVibration());
                    alarmMaker.setAlarmToRing(actPosition, Time.valueOf(mAlarm.getTime()), mAlarm.getDays(),
                            mAlarm.getRingtoneURIText(), mAlarm.getVibration());

                    holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.enabledAlarmColor));

                    /* Update MainActivity infoTextView accordingly */
                    MainActivity.activeAlarms++;
                    if(context instanceof MainActivity)
                        ((MainActivity)context).updateInfoTextView(MainActivity.activeAlarms);

                    Log.d("asdf", "Alarm status value in adapter: " + mAlarm.getStatus());
                } else {
                    mAlarmList.get(actPosition).setStatus(false);
                    MainActivity.databaseHelper.updateData(String.valueOf(actPosition + 1), mAlarm.getName(),
                            mAlarm.getTime(), mAlarm.getStatus(), mAlarm.getDays(),
                            mAlarm.getRingtoneURIText(), mAlarm.getVibration());
//                    alarmMaker.cancelAlarm(actPosition, Time.valueOf(mAlarm.getTime()), mAlarm.getDays(),
//                            mAlarm.getRingtoneURIText(), mAlarm.getVibration());

                    holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.disabledAlarmColor));

                    /* Update MainActivity infoTextView accordingly */
                    MainActivity.activeAlarms--;
                    if(context instanceof MainActivity)
                        ((MainActivity)context).updateInfoTextView(MainActivity.activeAlarms);


                    // position here, becomes request code for the pending intent in AlarmMaker
                    Log.d("asdf", "Alarm status value in adapter: " + mAlarm.getStatus());
                }

                // RESTART THE SERVICE
//                Intent serviceIntent = new Intent(context, AlarmService.class);
//                context.stopService(serviceIntent);
//                context.startService(serviceIntent);

                //Log.d("hjkl", "The updated alarm details are as follows: " + mAlarmList.get(actPosition).toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mAlarmList != null)
            return mAlarmList.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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
