package com.iblogstreet.rocket;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ClassName:RocketView <br/>
 * Function: 小火箭功能的实现 <br/>
 * Date: 2016年6月23日 下午2:52:13 <br/>
 * 在这里要理解<br/>
 * getRawX与getX的区别<br/>
 * getLocationOnScreen与getLocationInWindow的区别
 *
 * @author Army
 * @version
 */
public class RocketView
        implements OnTouchListener
{
    private static final String TAG = "RocketView";
    private Context context;

    /**
     * mWM:窗体管理器，用于添加，移除，更新视图.
     */
    private WindowManager mWM;

    public TextView getTvRocket() {
        return mTvRocket;
    }
    public void setRocketPercent(String percent){
        if(isRocketShowing){
           getTvRocket().setText("");
        }else
        getTvRocket().setText(percent);
    }

    /**
     * mIvRocket:小火箭视图.
     */
    private TextView                   mTvRocket;
    /**
     * mIvBottom:底部视图.
     */
    private ImageView                  mIvBottom;
    /**
     * mIvSmoke:火箭发射的烟火.
     */
    private ImageView                  mIvSmoke;
    /**
     * mRocketParams:火箭的视图参数(用一句话描述这个变量表示什么).
     */
    private WindowManager.LayoutParams mRocketParams;
    /**
     * mBottomParams:底部视图参数(用一句话描述这个变量表示什么).
     */
    private LayoutParams               mBottomParams;

    /**
     * windowX:屏幕宽度
     */
    private float   windowX;
    /**
     * windowY:屏幕高度.
     */
    private float   windowY;
    /**
     * isBottomCenter:小火箭中心点是否进入底部视图范围.
     */
    private boolean isEnterBottom;
    /**
     * isEnterBottomLast:用于记录小火箭上次是否进入底部视图范围的状态，以防止重复设置图片.
     */
    private boolean isEnterBottomLast;
    /**
     * isRocketShowing:小火箭是否显示.
     */
    private boolean isRocketShowing;
    private int[]   location;
    private float   startX;
    private float   startY;

    public RocketView(Context context) {
        this.context = context;
        mRocketParams = new WindowManager.LayoutParams();
        initRocketView(context);
        initBottomView();
        isEnterBottomLast = true;
        isEnterBottom = false;
        location = new int[2];
    }

    /**
     * 初始化底部<br/>
     */
    private void initBottomView() {
        // 底部图片
        mBottomParams = new WindowManager.LayoutParams();
        mBottomParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mBottomParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mBottomParams.format = PixelFormat.TRANSLUCENT;
        // params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        mBottomParams.type = LayoutParams.TYPE_PHONE;
        mBottomParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 初始化的话，管家图片放在右居中，由于改变了坐标方向，因此相关移动的方向数据也要改变
        mBottomParams.gravity = Gravity.BOTTOM | Gravity.CENTER_VERTICAL;

    }

    void showSmoke() {

        if (mIvSmoke == null) {
            mIvSmoke = new ImageView(context);
            mIvSmoke.setImageResource(R.drawable.desktop_smoke_t);

            mWM.addView(mIvSmoke, mBottomParams);
        }
    }

    void hideSmoke() {

        if (mIvSmoke != null) {

            if (mIvSmoke.getParent() != null) {

                mWM.removeView(mIvSmoke);
            }
            mIvSmoke = null;
        }
    }

    /**
     * 初始化火箭图片 <br/>
     *
     * @param context
     */
    private void initRocketView(Context context) {
        mWM = getWindowManager(context);
        mRocketParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mRocketParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mRocketParams.format = PixelFormat.TRANSLUCENT;
        // params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        mRocketParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mRocketParams.setTitle("Toast");
        mRocketParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 初始化的话，管家图片放在右居中，由于改变了坐标方向，因此相关移动的方向数据也要改变
        mRocketParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     *            必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWM == null) {
            mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWM;
    }

    public void show() {
        if (mIvBottom == null) {
            mIvBottom = new ImageView(context);
            mIvBottom.setImageResource(R.drawable.desktop_bg_tips_3);
            mIvBottom.setVisibility(View.GONE);
            mWM.addView(mIvBottom, mBottomParams);
        }

        if (mTvRocket == null) {
            mTvRocket = new TextView(context);
            mTvRocket.setBackground(context.getResources()
                                           .getDrawable(R.drawable.desktop_bg_2));
            mTvRocket.setGravity(Gravity.RIGHT);
            mTvRocket.setTextSize(16);
            mTvRocket.setOnTouchListener(this);
            mWM.addView(mTvRocket, mRocketParams);
        }

        DisplayMetrics metrics = context.getResources()
                                        .getDisplayMetrics();
        windowX = metrics.widthPixels;
        windowY = metrics.heightPixels;

    }

    /**
     * DESC :显示底部视图 . <br/>
     */
    private void showBottom() {

        if (!(isEnterBottomLast ^ isEnterBottom)) {
            return;
        }
        mIvBottom.setVisibility(View.VISIBLE);
        Log.i(TAG, "showBottom");
        if (!isEnterBottom) {
            AnimationDrawable animationDrawable = new AnimationDrawable();
            animationDrawable.setOneShot(false);
            animationDrawable.addFrame(context.getResources()
                                              .getDrawable(R.drawable.desktop_bg_tips_1), 80);
            animationDrawable.addFrame(context.getResources()
                                              .getDrawable(R.drawable.desktop_bg_tips_2), 80);
            mIvBottom.setImageDrawable(animationDrawable);
            animationDrawable.start();
        } else {
            mIvBottom.setImageResource(R.drawable.desktop_bg_tips_3);
        }

    }

    /**
     * DESC :隐藏底部视图 . <br/>
     */
    private void hideBottom() {
        if (mIvBottom != null) {
            mIvBottom.setVisibility(View.GONE);
        }
    }

    /**
     * DESC :隐藏小火箭 . 并停靠侧边<br/>
     */
    private void hideRocket(boolean flag) {
        if (mTvRocket != null) {
            if (flag) {
                mTvRocket.setBackground(context.getResources()
                                               .getDrawable(R.drawable.desktop_bg_2));
                mTvRocket.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            } else {
                mTvRocket.setBackground(context.getResources()
                                               .getDrawable(R.drawable.desktop_bg_1_1));
                mTvRocket.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            }
        }
    }

    private void showRocket() {
        if (isRocketShowing) {
            return;
        }
        Log.i(TAG, "setRocketAnimation");
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);
        animationDrawable.addFrame(context.getResources()
                                          .getDrawable(R.drawable.desktop_rocket_launch_1), 80);
        animationDrawable.addFrame(context.getResources()
                                          .getDrawable(R.drawable.desktop_rocket_launch_2), 80);
        mTvRocket.setBackground(animationDrawable);
        //   mIvRocket.setImageDrawable(animationDrawable);
        animationDrawable.start();
    }

    /**
     * DESC :界面销毁 . <br/>
     */
    public void removeView() {

        if (mTvRocket != null) {
            if (mTvRocket.getParent() != null) {

                mWM.removeView(mTvRocket);
            }
            mTvRocket = null;
        }
        if (mIvBottom != null) {
            if (mIvBottom.getParent() != null) {

                mWM.removeView(mIvBottom);
            }
            mIvBottom = null;
        }
    }


    float lastStartX;
    float lastStartY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                lastStartX =startX;
                lastStartY = startY;
                Log.i(TAG, "startX:" + startX + ",startY" + startY);
                break;
            case MotionEvent.ACTION_MOVE:
                //  mTvRocket.getLocationInWindow(location);
                //   Log.i(TAG, "getLocationInWindow:" + location[0] + "," + location[1]);
                mTvRocket.getLocationOnScreen(location);
                //Log.i(TAG, "getLocationOnScreen:" + location[0] + "," + location[1]);

                float currentX = event.getRawX();// -200
                float currentY = event.getRawY();
                Log.i(TAG, "current:" + currentX + "," + currentY);
                float distanceX = currentX - startX;
                float distanceY = currentY - startY;
                mRocketParams.x = (int) (mRocketParams.x - distanceX);
                mRocketParams.y = (int) (mRocketParams.y + distanceY);

                isEnterBottom = checkRocketWithBottomPostion(currentX, currentY);
                // 这里要设置一个标志位
                showRocket();
                showBottom();
                mWM.updateViewLayout(mTvRocket, mRocketParams);
                mWM.updateViewLayout(mIvBottom, mBottomParams);
                startX = currentX;
                startY = currentY;
                isEnterBottomLast = isEnterBottom;
                isRocketShowing = true;
                break;
            case MotionEvent.ACTION_UP:
                // 小火箭抬起后的事件，如果在中间的话，则改变变景，并且将小火箭移到中间
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (checkRocketWithBottomPostion(endX, endY)) {
                    // 如果小火箭中心点在底部背景范围内的，则首先将小火箭移到中心点,这需要一个移动动画
                    mWM.updateViewLayout(mTvRocket, mRocketParams);

                    launchRocket();
                } else {

                    boolean flag = checkRocketPostion(endX, endY);
                    hideBottom();
                    hideRocket(flag);
                    Log.e(TAG, "out");
                    mWM.updateViewLayout(mTvRocket, mRocketParams);
                    isRocketShowing = false;
                    isEnterBottom = false;
                    isEnterBottomLast = true;
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * DESC :发射小火箭 . <br/>
     */
    private void launchRocket() {
        // 如果小火箭中心点在底部背景范围内的，则首先将小火箭移到中心点,这需要一个移动动画
        // 得到底部中心点
        float bottomX = windowX / 2 - mTvRocket.getWidth() / 2;
        float bottomY = windowY - mTvRocket.getHeight();
        mRocketParams.x = (int) bottomX;
        mRocketParams.y = (int) bottomY;
        mWM.updateViewLayout(mTvRocket, mRocketParams);
        // 更改背景，加载腾云图片，隐藏
        // mIvBottom.setVisibility(View.INVISIBLE);
        //
        mIvBottom.setImageDrawable(context.getResources()
                                          .getDrawable(R.drawable.desktop_smoke_m));
        mWM.updateViewLayout(mIvBottom, mBottomParams);
        final int startY = (int) bottomY;
        final int endY   = -(int) windowY / 2;
        // 发射火箭
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startY, endY);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int y = (Integer) animation.getAnimatedValue();

                //  Log.e(TAG, "onAnimationUpdate" + y + "");
                mRocketParams.y = y;

                if (y == startY) {
                    showSmoke();
                }
                if (y <= endY) {
                    mIvBottom.setVisibility(View.INVISIBLE);
                    boolean flag=checkRocketPostion(lastStartX,lastStartY);
                    mRocketParams.y=0;
                    hideRocket(flag);
                    hideSmoke();
                    isRocketShowing = false;
                    isEnterBottom = false;
                    isEnterBottomLast = true;
                    mWM.updateViewLayout(mIvBottom, mBottomParams);
                }
                mWM.updateViewLayout(mTvRocket, mRocketParams);
            }
        });
        valueAnimator.start();
    }

    /**
     * DESC :检查小火箭是否在底部中心点范围 . <br/>
     *
     * @param x
     * @param y
     * @return
     */
    private boolean checkRocketWithBottomPostion(float x, float y) {
        // 如果小火箭在屏幕的中心点靠右的话，则往右停靠
        // 如果小火箭在屏幕中心点靠左的话，则往左停靠
        // 那怎样判断小火箭的中心点，以及屏幕的中心点
        float rocketCenter[] = calcRocketCenter(x, y);
        // 底部的位置
        mIvBottom.getLocationOnScreen(location);

        float   bottomX = mIvBottom.getWidth();// 底部的宽度
        float   bottomY = mIvBottom.getHeight();// 底部的高度
        boolean isXIn   = rocketCenter[0] > location[0] && rocketCenter[0] < (location[0] + bottomX);
        boolean isYIn   = rocketCenter[1] > location[1];

        return isXIn && isYIn;
    }

    /**
     * DESC : 传入火箭屏幕的坐标，计算出是靠左还是靠右 . <br/>
     *
     * @param x 小火箭屏幕坐标X
     * @param y 小火箭屏幕坐标Y
     * @return true 往左 false 往右
     */
    private boolean checkRocketPostion(float x, float y) {
        // 如果小火箭在屏幕的中心点靠右的话，则往右停靠
        // 如果小火箭在屏幕中心点靠左的话，则往左停靠
        // 那怎样判断小火箭的中心点，以及屏幕的中心点
        float rocketCenter[] = calcRocketCenter(x, y);
        // 屏幕中心点
        float screenCenterX = windowX / 2;
        if (rocketCenter[0] >= screenCenterX) {
            // 往右
            mRocketParams.x = 0;
            return true;
        } else {
            // 往左
            mRocketParams.x = (int) windowX;
            return false;
        }
    }


    /**
     * DESC :计算小火箭的中心点 . <br/>
     *
     * @param x
     * @param y
     * @return
     */
    private float[] calcRocketCenter(float x, float y) {
        float rocketX = mTvRocket.getWidth();// 小火箭的宽度
        float rocketY = mTvRocket.getHeight();// 小火箭的高度
        // 火箭中心点
        float rocketCenterX = x + rocketX / 2;
        float rocketCenterY = y + rocketY / 2;

        return new float[]{rocketCenterX,
                           rocketCenterY};
    }
}
