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
import android.media.AudioManager;

public class AudioController {

    private static AudioManager getManager(Context context)
    {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager;
    }
    
    public static boolean isRinger(Context context)
    {
        AudioManager audioManager = getManager(context);
        return !(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT);
    }
    
    public static void setEnabled(Context context, boolean enable)
    {
        AudioManager audioManager = getManager(context);
        int mode = enable ? AudioManager.RINGER_MODE_NORMAL : AudioManager.RINGER_MODE_SILENT;
        audioManager.setRingerMode(mode);
    }
}
