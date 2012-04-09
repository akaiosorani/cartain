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

import jp.srgtndr.akaiosorani.android.cartain.R;
import jp.srgtndr.akaiosorani.android.cartain.controller.AudioController;
import jp.srgtndr.akaiosorani.android.cartain.controller.BluetoothController;
import jp.srgtndr.akaiosorani.android.cartain.controller.DataTrafficController;
import jp.srgtndr.akaiosorani.android.cartain.controller.ScreenRotationController;
import jp.srgtndr.akaiosorani.android.cartain.controller.WifiController;
import com.angrydoughnuts.android.brightprof.BrightnessUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Cartain tool main Activity class
 * @author akaiosorani
 *
 */
public class CartainActivity extends Activity {

    final Messenger messnger = new Messenger(new IncomingHandler());

    Messenger serviceMessenger;
   
    boolean showPreference = false;
    
    private int originalBrightness;

    /**
     * Handle message for interaction cartain service
     * @author akaiosorani
     */
    class IncomingHandler extends Handler {
    	@Override
    	public void handleMessage(Message msg) {
    		switch(msg.what){
    		case CartainService.MSG_CHECK_CARTAIN:
    			// 
    			break;
    		default:
        	    super.handleMessage(msg);
    			break;
    		}
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cartain);
        originalBrightness = BrightnessUtil.getSystemBrightness(getContentResolver());
        int percent = 100;
        int value = percentToValue(percent);
        if (value >= originalBrightness) {
        	setBrightness(percent, true);
        } else {
        	SeekBar bar = (SeekBar)findViewById(R.id.brightness_slider);
        	percent = valueToPercent(originalBrightness);
        	bar.setProgress(percent);
        }
		TextView text = (TextView)findViewById(R.id.brightness_value);
		text.setEnabled(false);

        // start service 
        Intent service = new Intent(CartainActivity.this, CartainService.class);
        startService(service);        
        bindService();
       
        // setting for 3g/4g data
        ImageButton dataButton = (ImageButton)findViewById(R.id.data_button);
        dataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NetworkInfo.State state = DataTrafficController.getState(CartainActivity.this, ConnectivityManager.TYPE_MOBILE);
				if (state == NetworkInfo.State.UNKNOWN) {
					return;
				}
				int result;
				if (state == NetworkInfo.State.CONNECTED) {
					result = DataTrafficController.setMobileDisabled(CartainActivity.this);
				}else{
					result = DataTrafficController.setMobileEnabled(CartainActivity.this);
				}
				if (result != -1) {
					Log.i("cartain", "mobile state change success!");
				}
			}
		});

        // enable/disable Bluetooth
        ImageButton btButton = (ImageButton)findViewById(R.id.bt_button);
        btButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean current = BluetoothController.isEnabled(CartainActivity.this);
				BluetoothController.setEnabled(CartainActivity.this, !current);
			}
		});

        // enable/disable Wifi
        ImageButton wifiButton = (ImageButton)findViewById(R.id.wifi_button);
        wifiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean current = WifiController.isEnabled(CartainActivity.this);
				boolean result = WifiController.setEnabled(CartainActivity.this, !current);
				if (result) {
				}
			}
		});

        // call setting screen for GPS
        ImageButton gpsButton = (ImageButton)findViewById(R.id.gps_button);
        gpsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO check gps available device
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		});

        // enable/disable speaker
        ImageButton mannerButton = (ImageButton)findViewById(R.id.manner_button);
        mannerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioController.setEnabled(CartainActivity.this, !AudioController.isRinger(CartainActivity.this));
			}
		});

        // enable/disable auto rotation screen
        ImageButton rotationButton = (ImageButton)findViewById(R.id.autorotate_button);
        rotationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean current = ScreenRotationController.getAutoRotationEnabled(getContentResolver());
				ScreenRotationController.setAutoRotationEnabled(getContentResolver(), !current);
			}
		});

        // enable/disable flight mode
        ImageButton flightModeButton = (ImageButton)findViewById(R.id.flight_button);
        flightModeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int current = 0;
				try {
	                current = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
                } catch (SettingNotFoundException e) {
                	// TODO error handling
	                e.printStackTrace();
	                return;
                }
				int settingValue = current > 0 ? 0 : 1;
				Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, settingValue);
			    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			    intent.putExtra("state", settingValue);
			    sendBroadcast(intent);
			    // TODO receive state and change button enabled
			}
		});
        
        SeekBar brightnessSlider = (SeekBar)findViewById(R.id.brightness_slider);
        brightnessSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setBrightness(progress+1, false);
			}
		});

        // close this activity
        Button closeButton = (Button)findViewById(R.id.close_button);
        closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox revertClosing = (CheckBox)findViewById(R.id.revert_brightness);
				if (revertClosing.isChecked()) {
					int current = BrightnessUtil.getSystemBrightness(getContentResolver());
					if (current > originalBrightness)
					{
			        	BrightnessUtil.setSystemBrightness(getContentResolver(), getWindow(), originalBrightness);
					}
				}
				startCartain();
				finish();
			}
		});
        // exit this application
        Button exitButton = (Button)findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopCartain();
				// TODO stop service
				finish();
			}
		});
        
        Window w = getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        params.alpha = 0.85f;
        w.setAttributes(params);
        
        TranslateAnimation animation = 
                new TranslateAnimation(
            		Animation.RELATIVE_TO_PARENT, -1.0f,
            		Animation.RELATIVE_TO_PARENT, 0.0f, 
            		Animation.RELATIVE_TO_PARENT, 0.0f,
            		Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        View rootView = findViewById(R.id.main);
        rootView.startAnimation(animation);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	if(hasFocus || showPreference)
    	{
    		hideCartain();
    	}else
    	{
    		showCartain();
    	}
        super.onWindowFocusChanged(hasFocus);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getString(R.string.menu_preferences));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
    	showPreference = true;
        return super.onMenuOpened(featureId, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.menu_preferences))) {
            Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }    
    
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	showPreference = false;
        super.onOptionsMenuClosed(menu);
    }
        
    private void startCartain()
    {
    	sendMessage(CartainService.MSG_START_CARTAIN);
    }

    private void stopCartain()
    {
    	sendMessage(CartainService.MSG_STOP_CARTAIN);
    }
    
    private void showCartain()
    {
    	sendMessage(CartainService.MSG_SHOW_CARTAIN);
    }
    
    private void hideCartain()
    {
    	sendMessage(CartainService.MSG_HIDE_CARTAIN);
    }
    
    private void sendMessage(int msg)
    {
    	if (serviceMessenger == null) {
    		return;
    	}
    	
		Message message = Message.obtain();
		message.replyTo = messnger;
		message.what = msg;
		try {
	        serviceMessenger.send(message);
        } catch (RemoteException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }
    
	ServiceConnection con = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceMessenger = new Messenger(service);
			Message message = Message.obtain();
			message.replyTo = messnger;
			message.what = CartainService.MSG_CHECK_CARTAIN;
			try {
	            serviceMessenger.send(message);
            } catch (RemoteException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
	};

    private void bindService()
    {
        bindService(new Intent(this, CartainService.class), con, Context.BIND_AUTO_CREATE);
    }

    private void unbindService()
    {
    	unbindService(con);
    }
    
    private void setBrightness(int percent, boolean isSetSeekbar) {
    	int value = percentToValue(percent);
    	BrightnessUtil.setSystemBrightness(getContentResolver(), getWindow(), value);
    	if (isSetSeekbar){
    		SeekBar bar = (SeekBar)findViewById(R.id.brightness_slider);
    		bar.setProgress(percent);
    	}
		TextView text = (TextView)findViewById(R.id.brightness_value);
		String t = Integer.toString(percent);
		text.setText(t);
    }

    private static int percentToValue(int percent)
    {
        int max = 255;
        int min = 10;
        int p = percent;
        p = (p>100) ? 100 : (p<0) ? 0 : p;
        
        double value = min + ((max - min) / 100.0 * p);
        return (int)Math.floor(value);
    }
    
    private static int valueToPercent(int value)
    {
        int max = 255;
        int min = 10;
        int p = (int)(100.0 * (value - min) / (max - min));
        p = (p > 100.0f) ? 100 : (p < 1 ? 1 : p);
        return p;
    }
    
}
