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

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;

public class AudioController {

    private static boolean useSilent = false;

    private static AudioManager getManager(Context context)
    {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager;
    }
    private static int getMode(boolean enabled)
    {
        if (enabled)
        {
            return AudioManager.RINGER_MODE_NORMAL;
        }else {
            return useSilent ? AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_VIBRATE;
        }
    }

    public static int getMode(Context context)
    {
        AudioManager audioManager = getManager(context);
        return audioManager.getRingerMode();
    }
    public static boolean isRinger(Context context)
    {
        return getMode(context) == AudioManager.RINGER_MODE_NORMAL;
    }

    public static void setRingerEnabled(Context context, boolean enabled)
    {
        AudioManager audioManager = getManager(context);
        audioManager.setRingerMode(getMode(enabled));
    }
    public static IntentFilter getFilter()
    {
        return new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
    }
}
