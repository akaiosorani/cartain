package jp.srgtndr.akaiosorani.android.cartain.controller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;

public class GpsController {

    public static LocationManager getManager(Context context)
    {
        return (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static void callSetting(Context context)
    {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
    public static boolean isGpsDevice(Context context) 
    {
        LocationManager manager = getManager(context);
        return manager.getAllProviders().contains(LocationManager.GPS_PROVIDER);
    }

    public static boolean isGpsEnabled(Context context)
    {
        LocationManager manager = getManager(context);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static IntentFilter getFilter()
    {
        return new IntentFilter(LocationManager.KEY_PROVIDER_ENABLED);
    }
}
