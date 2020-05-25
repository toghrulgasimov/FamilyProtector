package com.family.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.family.familyprotector.MainActivity;
import com.family.familyprotector.ParentActivity;

public class BootDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                Intent activityIntent = new Intent(context, ParentActivity.class);

                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            }catch (Exception e){}


        }
    }
}
