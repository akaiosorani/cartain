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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class DataTrafficController {

    private static int TARGET_DEVICE_TYPE = ConnectivityManager.TYPE_MOBILE;

    private static String ENABLE_METHOD_NAME = "enableDataConnectivity";
    private static String DISABLE_METHOD_NAME = "disableDataConnectivity";

    private static ConnectivityManager getManager(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager;
    }

    private static NetworkInfo getNetworkInfo(Context context, int deviceType)
    {
        ConnectivityManager manager = getManager(context);
        NetworkInfo[] networks = manager.getAllNetworkInfo();
        for(NetworkInfo info : networks)
        {
            if (info.getType() == deviceType)  {
                return info;
            }
        }
        return null;
    }

    public static boolean isAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context, TARGET_DEVICE_TYPE);
        return (info != null) && info.isAvailable();
    }

    public static boolean isDevice(Context context)
    {
        NetworkInfo info = getNetworkInfo(context, TARGET_DEVICE_TYPE);
        return (info != null);
    }

    public static NetworkInfo.State getState(Context context)
    {
        NetworkInfo info = getNetworkInfo(context, TARGET_DEVICE_TYPE);
        return (info != null) ? info.getState() : NetworkInfo.State.UNKNOWN;
    }

    private static TelephonyManager getTelephoneyManager(Context context)
    {
        return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }
    
    public static void setMobileEnabled(Context context, boolean enabled)
    {
        TelephonyManager manager = getTelephoneyManager(context);
        boolean current = (manager.getDataState() == TelephonyManager.DATA_CONNECTED);
        if (current == enabled)
        {
            return;
        }

        try {
            Class managerClass = Class.forName(manager.getClass().getName());
            Method getITelephonyMethod = managerClass.getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            Object iTelephony = getITelephonyMethod.invoke(manager);
            Class iTelephonyClass = Class.forName(iTelephony.getClass().getName());
            String methodName = enabled ? ENABLE_METHOD_NAME : DISABLE_METHOD_NAME;
            Method connectMethod = iTelephonyClass.getDeclaredMethod(methodName);
            connectMethod.setAccessible(true);
            connectMethod.invoke(iTelephony);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static IntentFilter getFilter()
    {
        return new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }
}
