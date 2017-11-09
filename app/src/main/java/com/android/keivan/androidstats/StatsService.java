package com.android.keivan.androidstats;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import java.util.Random;

public class StatsService extends Service
{
    private int UUID;
    private Random random;
    private TextView floatingTextView;
    private WindowManager windowManager;
    private static boolean overlayPermission;
    private static stats Statistics;
    private Handler handler;


    // Default Values
    private static final int DEFAULT_TEXT_COLOR = Color.argb(255, 225, 100, 30);
    private static final int DEFAULT_BACKGROUND_COLOR = Color.argb(64,200,200,200);
    private static final int UPDATE_DURATION = 1000;

    // Customization Variables
    int backgroundColor,textColor;

    public static void setOverlayPermission(boolean overlayPermission)
    {
        StatsService.overlayPermission = overlayPermission;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Statistics = new stats(getBaseContext());
        handler = new Handler();

        MainActivity.overlayDrawn=true;
        random = new Random();
        UUID = random.nextInt(1000);
        Log.d("Floating", "UUID=" + UUID);
        floatingTextView = new TextView(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        textColor = pref.getInt(getString(R.string.overlay_text_color),DEFAULT_TEXT_COLOR);
        backgroundColor = pref.getInt(getString(R.string.overlay_background_color),DEFAULT_BACKGROUND_COLOR);
        pref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences pref, String s)
            {
                textColor = pref.getInt(getString(R.string.overlay_text_color),DEFAULT_TEXT_COLOR);
                backgroundColor = pref.getInt(getString(R.string.overlay_background_color),DEFAULT_BACKGROUND_COLOR);
                if(floatingTextView!=null)
                {
                    floatingTextView.setTextColor(textColor);
                    floatingTextView.setBackgroundColor(backgroundColor);
                }
            }
        });
        drawFloatingStats();

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                floatingTextView.setText(Statistics.getStats());
                handler.postDelayed(this,UPDATE_DURATION);
                return;
            }
        },UPDATE_DURATION);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        MainActivity.overlayDestroy();
        Log.d("Floating", "Inside onDestroy of Floating class");
        Log.d("Floating", "OnDestroy UUID=" + UUID);
        if(floatingTextView!=null)
            windowManager.removeView(floatingTextView);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void drawFloatingStats()
    {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingTextView.setTextColor(textColor);
        floatingTextView.setBackgroundColor(backgroundColor);
        //here is all the science of params
        final LayoutParams myParams = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        myParams.x = displayMetrics.widthPixels;
        myParams.y = 10;

        windowManager.addView(floatingTextView, myParams);
        try
        {

            floatingTextView.setOnTouchListener(new View.OnTouchListener()
            {
                WindowManager.LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    //remove overlay on long press
                    if(System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getRawX())
                    {
                        stopSelf();
                        return false;
                    }
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            Log.d("onTouch", "initialTouchX = " + initialTouchX);
                            Log.d("onTouch", "event.getX() = " + event.getX());
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(v, myParams);
                            break;
                    }
                    return false;
                }
            });
            floatingTextView.setText(Statistics.getStats());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
