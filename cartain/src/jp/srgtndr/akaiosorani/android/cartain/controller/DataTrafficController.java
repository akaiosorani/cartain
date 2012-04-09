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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class DataTrafficController {

	private static String FEATURE_ENABLE = "enableHIPRI";
	private static String FEATURE_DISABLE = "disableHIPRI";
	
	private static ConnectivityManager getManager(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager;
	}
	
	public static void test(Context context)
	{
		// mobile, WIFI, mobile_mms, mobile_dun, mobile_supl, mobile_hipri
		ConnectivityManager manager = getManager(context);
		NetworkInfo[] networks = manager.getAllNetworkInfo();
		for(NetworkInfo info : networks)
		{
			Log.i("test", info.getTypeName());
		}
	}
	
	public static boolean isDevice(Context context, int deviceType) {
		ConnectivityManager manager = getManager(context);
		NetworkInfo[] networks = manager.getAllNetworkInfo();
		for(NetworkInfo info : networks)
		{
			if (info.getType() == deviceType)  {
				return true;
			}
		}
		return false;
	}
	
	public static NetworkInfo.State getState(Context context, int deviceType)
	{
		ConnectivityManager manager = getManager(context);
		NetworkInfo[] networks = manager.getAllNetworkInfo();
		for(NetworkInfo info : networks)
		{
			if (info.getType() == deviceType)  {
				info.getState();
			}
		}
		manager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, FEATURE_ENABLE);
		return NetworkInfo.State.UNKNOWN;
	}
	
	public static int setMobileEnabled(Context context)
	{
		ConnectivityManager manager = getManager(context);
		return manager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, FEATURE_ENABLE);
	}
	public static int setMobileDisabled(Context context)
	{
		ConnectivityManager manager = getManager(context);
		return manager.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, FEATURE_DISABLE);
	}
}
