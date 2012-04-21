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

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug.MemoryInfo;

public class AppInfo {

    private static MemoryInfo getCurrentAppMemory(Context context)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] pids = new int[1];
        pids[0] = android.os.Process.myPid();
        MemoryInfo[] mis = am.getProcessMemoryInfo(pids);
        return mis[0];
    }
    public static String getMemoryInfo(Context context)
    {
        MemoryInfo mi = getCurrentAppMemory(context);
        return String.format("%d KB %d KB %d KB", mi.getTotalPss(), mi.getTotalSharedDirty(), mi.getTotalPrivateDirty());
    }
    public static int getConsumedMemory(Context context)
    {
        return getCurrentAppMemory(context).getTotalPss();
    }

}
