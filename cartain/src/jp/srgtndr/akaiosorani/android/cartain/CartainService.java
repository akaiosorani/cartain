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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CartainService extends Service {

    static final int MSG_START_CARTAIN = 1;

    static final int MSG_STOP_CARTAIN = 2;

    static final int MSG_SHOW_CARTAIN = 3;

    static final int MSG_HIDE_CARTAIN = 4;

    static final int MSG_CHECK_CARTAIN = 5;

    private static final String VIEW_CONTROL = "view_control";

    private boolean alreadyStarted = false;

    private final Messenger messenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
            case MSG_START_CARTAIN:
                startCartain();
                break;
            case MSG_STOP_CARTAIN:
                stopCartain();
                break;
            case MSG_SHOW_CARTAIN:
                showCartain();
                break;
            case MSG_HIDE_CARTAIN:
                hideCartain();
                break;
            case MSG_CHECK_CARTAIN:
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate()
    {
        Log.d("cartain", "service created");
        startCartain();
    }

    @Override
    public void onDestroy()
    {
        CartainView.destroyView(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private void startCartain()
    {
        synchronized (VIEW_CONTROL) {
            CartainView v = CartainView.createView(getApplicationContext());
            v.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        startSettings();
                    }
                    return true;
                }
            });
            alreadyStarted = true;
        }
    }

    private void stopCartain()
    {
        synchronized (VIEW_CONTROL) {
            CartainView.destroyView(getApplicationContext());
            alreadyStarted = false;
        }
    }

    private void showCartain()
    {
        if(!alreadyStarted) {
            return;
        }
        CartainView.show();
    }

    private void hideCartain()
    {
        if(!alreadyStarted) {
            return;
        }
        CartainView.hide();
    }

    private void startSettings()
    {
        Intent intent = new Intent(this, CartainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
