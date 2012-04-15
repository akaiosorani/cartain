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
import jp.srgtndr.akaiosorani.android.cartain.controller.AirplaneModeController;
import jp.srgtndr.akaiosorani.android.cartain.controller.AudioController;
import jp.srgtndr.akaiosorani.android.cartain.controller.BluetoothController;
import jp.srgtndr.akaiosorani.android.cartain.controller.DataTrafficController;
import jp.srgtndr.akaiosorani.android.cartain.controller.ScreenRotationController;
import jp.srgtndr.akaiosorani.android.cartain.controller.WifiController;
import com.angrydoughnuts.android.brightprof.BrightnessUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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

    private final Messenger messnger = new Messenger(new IncomingHandler());

    private Messenger serviceMessenger;

    private boolean showPreference = false;

    private int originalBrightness;

    private BroadcastReceiver receiver;
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

        // brightness setting when opening
        originalBrightness = BrightnessUtil.getSystemBrightness(getContentResolver());
        Log.d("cartain", String.format("original brightness: %d", originalBrightness));
        int percent = 60;
        int value = percentToValue(percent);
        if (value >= originalBrightness) {
            setBrightness(percent, true);
        } else {
            percent = valueToPercent(originalBrightness);
            setBrightnessInfo(percent, true);
        }

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
                    Log.d("cartain", "mobile state change success!");
                }
            }
        });
        if (!DataTrafficController.isDevice(this, ConnectivityManager.TYPE_MOBILE)) 
        {
            setButtonEnabled(dataButton, false);
        }

        // enable/disable Bluetooth
        ImageButton btButton = (ImageButton)findViewById(R.id.bt_button);
        btButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean current = BluetoothController.isEnabled(CartainActivity.this);
                BluetoothController.setEnabled(CartainActivity.this, !current);
            }
        });
        if (!BluetoothController.isDevice(this)) 
        {
            setButtonEnabled(btButton, false);
        }

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
        if (!WifiController.isWifiDevice(this))
        {
            setButtonEnabled(wifiButton, false);
        }

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
                AirplaneModeController.changeAirplaneMode(CartainActivity.this);
            }
        });

        // set seekbar handler for brightness cotnrol
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
                Log.d("cartain", String.format("original brightness: %d current: %d", originalBrightness, BrightnessUtil.getSystemBrightness(getContentResolver())));
                if (revertClosing.isChecked()) {
                    BrightnessUtil.setSystemBrightness(getContentResolver(), getWindow(), originalBrightness);
                }
                startCartain();
                Log.d("cartain", String.format("current: %d", BrightnessUtil.getSystemBrightness(getContentResolver())));
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

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("cartain", "Connectivity changed");
                updateButtonStatus();
            }
        };
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(receiver, BluetoothController.getFilter());
        registerReceiver(receiver, WifiController.getFilter());
        registerReceiver(receiver, AirplaneModeController.getFilter());
        updateButtonStatus();

        // set background transparency
        Window w = getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        params.alpha = 0.85f;
        w.setAttributes(params);

        // set animation for opening
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
        unregisterReceiver(receiver);
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

    /**
     * add icon for launch 
     */
    private void startCartain()
    {
        sendMessage(CartainService.MSG_START_CARTAIN);
    }

    /**
     * remove icon for launch
     */
    private void stopCartain()
    {
        sendMessage(CartainService.MSG_STOP_CARTAIN);
    }

    /**
     * overlay icon for launch
     */
    private void showCartain()
    {
        sendMessage(CartainService.MSG_SHOW_CARTAIN);
    }

    /**
     * hide icon for launch
     */
    private void hideCartain()
    {
        sendMessage(CartainService.MSG_HIDE_CARTAIN);
    }

    /**
     * utility method for button enable/disable
     * @param button
     * @param enabled
     */
    private void setButtonEnabled(ImageButton button, boolean enabled)
    {
        button.setEnabled(enabled);
    }

    /**
     * send message to a service to control overlay icon
     * @param msg action type of message
     */
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

    private void updateButtonStatus()
    {
        ImageButton dataButton = (ImageButton)findViewById(R.id.data_button);
        ImageButton btButton = (ImageButton)findViewById(R.id.bt_button);
        btButton.setImageResource(BluetoothController.isEnabled(this) ? R.drawable.bt2 : R.drawable.bt);
        ImageButton wifiButton = (ImageButton)findViewById(R.id.wifi_button);
        wifiButton.setImageResource(WifiController.isEnabled(this) ? R.drawable.wifi2 : R.drawable.wifi);
        ImageButton gpsButton = (ImageButton)findViewById(R.id.gps_button);
        ImageButton flightModeButton = (ImageButton)findViewById(R.id.flight_button);
        flightModeButton.setImageResource(AirplaneModeController.isAirplaneModeOn(this) ? R.drawable.airplane2 : R.drawable.airplane);
        ImageButton mannerButton = (ImageButton)findViewById(R.id.manner_button);
    }

    private void setBrightness(int percent, boolean updateSeekbar) {
        int value = percentToValue(percent);
        BrightnessUtil.setSystemBrightness(getContentResolver(), getWindow(), value);
        setBrightnessInfo(percent, updateSeekbar);
    }

    private void setBrightnessInfo(int percent, boolean updateSeekbar)
    {
        if (updateSeekbar){
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
        int min = 5;
        int p = percent;
        p = (p>100) ? 100 : (p<0 ? 0 : p);
        
        double value = min + ((max - min) / 100.0 * p);
        return (int)Math.floor(value);
    }

    private static int valueToPercent(int value)
    {
        int max = 255;
        int min = 5;
        int p = (int)(100.0 * (value - min) / (max - min));
        p = (p > 100.0f) ? 100 : (p < 1 ? 1 : p);
        return p;
    }

}
