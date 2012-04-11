package com.angrydoughnuts.android.brightprof;
/****************************************************************************
 * Copyright 2009 kraigs.android@gmail.com
 * Copyright 2012 akaiosorani(akaiosorani@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ****************************************************************************/

import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.view.WindowManager;

/**
 * Some util functions for brightness control
 */
public class BrightnessUtil {

  /**
   * Finds the phone's system brightness setting. Returns 0 if there is an error
   * getting this setting.
   * 
   * @param resolver
   *          The ContentResolver.
   * @return A value between 0 and 255.
   */
  public static int getSystemBrightness(ContentResolver resolver) {
    // Lookup the initial system brightness.
    int systemBrightness = 0;
    try {
      systemBrightness = Settings.System.getInt(resolver,
          Settings.System.SCREEN_BRIGHTNESS);
    } catch (SettingNotFoundException e) {
      // TODO Log an error message.
    }
    return systemBrightness;
  }

  public static void setSystemBrightness(ContentResolver resolver, Window window, int brightnessUnits) {
      setSystemBrightness(resolver, brightnessUnits);
      setActivityBrightness(window, brightnessUnits);
  }
  
  
  /**
   * Sets the phone's global brightness level. This does not change the screen's
   * brightness immediately. Valid brightnesses range from 0 to 255.
   * 
   * @param resolver
   *          The ContentResolver.
   * @param brightnessUnits
   *          An integer between 0 and 255.
   */
  static void setSystemBrightness(ContentResolver resolver, int brightnessUnits) {
    // Change the system brightness setting. This doesn't change the
    // screen brightness immediately. (Scale 0 - 255).
    Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightnessUnits);
  }

  /**
   * Sets the screen brightness for this activity. The screen brightness will
   * change immediately. As soon as the activity terminates, the brightness will
   * return to the system brightness. Valid brightnesses range from 0 to 255.
   * 
   * @param window
   *          The activity window.
   * @param brightnessUnits
   *          An integer between 0 and 255.
   */
  static void setActivityBrightness(Window window, int brightnessUnits) {
    // Set the brightness of the current window. This takes effect immediately.
    // When the window is closed, the new system brightness is used.
    // (Scale 0.0 - 1.0).
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.screenBrightness = brightnessUnits / 255.0f;
    window.setAttributes(lp);
  }

  // These constants are not exposed through the API, but are defined in
  // Settings.System:
  // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/android/provider/Settings.java;h=f7e55db80b8849c023152ad06d97040199c4e8c5;hb=HEAD
  private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
  private static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
  private static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
  public static boolean supportsAutoBrightness(ContentResolver resolver) {
    // This is probably not the best way to do this.  The actual capability
    // is stored in
    // com.android.internal.R.bool.config_automatic_brightness_available
    // which is not available through the API.
    try {
      Settings.System.getInt(resolver, SCREEN_BRIGHTNESS_MODE);
      return true;
    } catch (SettingNotFoundException e) {
      return false;
    }
  }

  public static boolean getAutoBrightnessEnabled(ContentResolver resolver) {
    try {
      int autobright = Settings.System.getInt(resolver, SCREEN_BRIGHTNESS_MODE);
      return autobright == SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    } catch (SettingNotFoundException e) {
      return false;
    }
  }

  public static void setAutoBrightnessEnabled(ContentResolver resolver, boolean enabled) {
    if (enabled) {
      Settings.System.putInt(resolver, SCREEN_BRIGHTNESS_MODE,
          SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    } else {
      Settings.System.putInt(resolver, SCREEN_BRIGHTNESS_MODE,
          SCREEN_BRIGHTNESS_MODE_MANUAL);
    }
  }
}
