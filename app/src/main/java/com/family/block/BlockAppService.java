package com.family.block;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class BlockAppService {


    public BlockAppService(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("package name");
    }
}
