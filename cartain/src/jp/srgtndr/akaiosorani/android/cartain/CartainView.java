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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CartainView extends FrameLayout {

    private static CartainView view = null;
    
    CartainView(Context context) {
        super(context);
        setLayout(context);
    }
    
    protected void setLayout(Context context)
    {
        setBackgroundColor(Color.GREEN);

        LayoutParams params = this.generateDefaultLayoutParams();
        params.gravity = Gravity.LEFT;

        TextView label = new TextView(context);
        label.setText("★");
        label.setTextSize(40);
        addView(label, params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
        
    public static CartainView createView(Context context) {
        CartainView v = new CartainView(context);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                50, 50, 
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.alpha = 0.3f;
        params.gravity = Gravity.LEFT  |Gravity.CENTER_VERTICAL;
        wm.addView(v, params);        
        view = v;
        return v;
    }
    
    public static void destroyView(Context context)
    {
        if (view==null) return;
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        // TODO fix error
        wm.removeView(view);
    }
    
    public static void show()
    {
        if(view==null){
            return;
        }
        view.setVisibility(View.VISIBLE);
    }
    
    public static void hide()
    {
        if(view==null){
            return;
        }
        view.setVisibility(View.INVISIBLE);
    }
}
