package my.app.manavb.voicealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        After the device has booted, it receives this broadcast and schedules the alarms accordingly.
         */
//        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
//            context.stopService(new Intent(context, AlarmService.class));
//            context.startService(new Intent(context, AlarmService.class));
//        } else{
//            context.stopService(new Intent(context, AlarmService.class));
//            context.startService(new Intent(context, AlarmService.class));
//        }








    }
}
