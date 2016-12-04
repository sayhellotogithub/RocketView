
package com.iblogstreet.rocket;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * ClassName:LocationToast <br/>
 * Function: 实现一个自定义的Toast. <br/>
 * Date: 2016年6月23日 下午3:31:06 <br/>
 * 
 * @author Army
 * @version
 */
public class LocationToast implements OnTouchListener {

    private Context context;
    private WindowManager mWM;
    private TextView tView;
    private WindowManager.LayoutParams params;
    private float startX;
    private float startY;

    public LocationToast(Context context) {
        this.context = context;
        params = new WindowManager.LayoutParams();
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        // params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

    }

    public void show() {
        if (tView == null)
            tView = new TextView(context);
        tView.setText("heflefle");
        //tView.s(context.getResources().getDrawable(R.drawable.desktop_bg_2));
        tView.setTextColor(Color.WHITE);
        tView.setBackgroundColor(Color.BLACK);
        tView.setOnTouchListener(this);
        if (tView != null) {

        }
        mWM.addView(tView, params);
    }

    public void hide() {
        if (tView != null) {
            // note: checking parent() just to make sure the view has
            // been added... i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (tView.getParent() != null) {

                mWM.removeView(tView);
            }

            tView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getRawX();
                float currentY = event.getRawY();
                float distanceX = currentX - startX;
                float distanceY = currentY - startY;

                params.x = (int) (params.x + distanceX);
                params.y = (int) (params.y + distanceY);
                mWM.updateViewLayout(tView, params);
                startX = currentX;
                startY = currentY;
                break;
            default:
                break;
        }
        return true;
    }

}
