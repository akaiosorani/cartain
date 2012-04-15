package jp.srgtndr.akaiosorani.android.cartain.controller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;

public class AirplaneModeController {

    public static void changeAirplaneMode(Context context)
    {
        boolean currentMode = isAirplaneModeOn(context);
        int settingValue = currentMode ? 0 : 1;
        Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, settingValue);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", settingValue);
        context.sendBroadcast(intent);
    }

    public static boolean isAirplaneModeOn(Context context)
    {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    public static IntentFilter getFilter()
    {
        return new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    }
}
