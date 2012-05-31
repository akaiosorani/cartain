/*
 * Copyright 2012 akaiosorani(akaiosorani@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.srgtndr.akaiosorani.android.cartain;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    
    private static final boolean DEFAULT_STARTUP_ON_BOOT = false;
    private static final boolean DEFAULT_BRIGHT_WITH_DIALOG = true;
    private static final boolean DEFAULT_REVERT_BRIGHTNESS = true;
    private static final boolean DEFAULT_SHOW_BATTERY = false;
    private static final int DEFAULT_BRIGHTNESS = 60;
    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public static void setDefaultValue(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        // start up on boot
        String key = context.getString(R.string.pref_key_startup);
        editor.putBoolean(key, prefs.getBoolean(key, DEFAULT_STARTUP_ON_BOOT));

        // icon position
        key = context.getString(R.string.pref_key_position);
        String[] values = context.getResources().getStringArray(R.array.pref_values_position);
        editor.putString(key, prefs.getString(key, values[0]));

        // brightness 
        key = context.getString(R.string.pref_key_bright_with_dialog);
        editor.putBoolean(key, prefs.getBoolean(key, DEFAULT_BRIGHT_WITH_DIALOG));

        // brightness value
        key = context.getString(R.string.pref_key_brightness);
        editor.putInt(key, prefs.getInt(key, DEFAULT_BRIGHTNESS));

        // revert brightness
        key = context.getString(R.string.pref_key_revert_brightness);
        editor.putBoolean(key, prefs.getBoolean(key, DEFAULT_REVERT_BRIGHTNESS));

        // battery
        key = context.getString(R.string.pref_key_show_battery);
        editor.putBoolean(key, prefs.getBoolean(key, DEFAULT_SHOW_BATTERY));

        // silent/manner
        key = context.getString(R.string.pref_key_silent_mode);
        values = context.getResources().getStringArray(R.array.pref_values_silent);
        editor.putString(key, prefs.getString(key, values[0]));

        editor.commit();
    }

    public static boolean isStartupOnBoot(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_startup), DEFAULT_STARTUP_ON_BOOT);
    }
    public static boolean isBrightWithDialog(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_bright_with_dialog), DEFAULT_BRIGHT_WITH_DIALOG);
    }
    public static boolean isRevertBrightness(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_revert_brightness), DEFAULT_REVERT_BRIGHTNESS);
    }
    public static int getBrightness(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        int brightness = prefs.getInt(context.getString(R.string.pref_key_brightness), DEFAULT_BRIGHTNESS);
        if (brightness > 100) brightness = 100;
        if (brightness < 5) brightness = 5;
        return brightness;
    }
    public static boolean isIconOnLeft(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        String[] values = context.getResources().getStringArray(R.array.pref_values_position);
        String iconSetting = prefs.getString(context.getString(R.string.pref_key_position), values[0]);
        return values[0].equals(iconSetting);
    }
    public static boolean isMannerMode(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(context);
        String[] values = context.getResources().getStringArray(R.array.pref_values_silent);
        String silentSetting = prefs.getString(context.getString(R.string.pref_key_silent_mode), values[0]);
        return values[0].equals(silentSetting);
    }

}
