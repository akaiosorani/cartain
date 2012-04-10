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

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothController {

	private static BluetoothAdapter getAdapter(Context context)
	{
		return BluetoothAdapter.getDefaultAdapter();
	}
	
	public static boolean setEnabled(Context context, boolean enabled)
	{
		BluetoothAdapter adapter = getAdapter(context);
		return enabled ? adapter.enable() : adapter.disable();
	}
	public static boolean isEnabled(Context context)
	{
		return getAdapter(context).isEnabled();
	}
	
	public static int getState(Context context)
	{
		return getAdapter(context).getState();
	}
	
	public static boolean isDevice(Context context)
	{
		BluetoothAdapter adapter = getAdapter(context);
		return adapter != null;
	}
	
}
