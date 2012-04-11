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
package jp.srgtndr.akaiosorani.android.cartain.controller;

import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

/**
 * operation for screen rotation lock
 * @author akaiosorani
 *
 */
public class ScreenRotationController {

    public static boolean getAutoRotationEnabled(ContentResolver resolver) {
        int current = 0;
        try {
            current = Settings.System.getInt(resolver, Settings.System.ACCELEROMETER_ROTATION);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return current == 1 ? true : false;
    }

    public static void setAutoRotationEnabled(ContentResolver resolver, boolean enabled)
    {
        Settings.System.putInt(resolver, Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }
}
