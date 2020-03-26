package com.family.AppActivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.family.util.Util;

import java.util.Date;
import java.util.List;

public class AppActivityService {
    //https://stackoverflow.com/questions/4212992/how-can-i-check-if-an-app-running-on-android
    //https://www.google.com/search?newwindow=1&sxsrf=ALeKk00hZyxq676SK67feRAy2SJvoWDw0w%3A1584905577045&ei=ab13XuOoAo-csAfks7z4BA&q=stackoverflow+detect+running+app+androud
    //https://stackoverflow.com/questions/2695746/how-to-get-a-list-of-installed-android-applications-and-pick-one-to-run

    //get app icon
    //https://stackoverflow.com/questions/17985500/how-can-i-get-the-applications-icon-from-the-package-name


    // Usage Access Permission

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void getStatus(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> S = manager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY,
                0, System.currentTimeMillis());
        for(UsageStats s : S) {
            if (true && !s.getPackageName().startsWith("com.android")) {

                Date d = new Date(s.getLastTimeUsed());
                String[] dd = d.toString().split(" ");
                dd[3] = dd[3].substring(0, 5);
                Log.d("last", s.getPackageName() + "--- " + (dd[2] + "-" + dd[1] + "-" + dd[3]));

                Log.d("usage", s.getPackageName() + "-" + Util.milliToTime(s.getTotalTimeInForeground()));
            }

        }
    }
}
