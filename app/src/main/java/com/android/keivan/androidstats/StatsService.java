package com.android.keivan.androidstats;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.Preference;
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

    // Customization Variables
    Color backgroundColor;
    Color textColor;


    public static void setOverlayPermission(boolean overlayPermission)
    {
        StatsService.overlayPermission = overlayPermission;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        random = new Random();
        UUID = random.nextInt(1000);
        Log.d("Floating", "UUID=" + UUID);
        floatingTextView = new TextView(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Log.i("Pref",pref.getAll().toString());

        if(overlayPermission)
        {
            drawFloatingStats();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy()
    {
        Log.d("Floating", "Inside onDestroy of Floating class");
        Log.d("Floating", "OnDestroy UUID=" + UUID);
        windowManager.removeView(floatingTextView);
//        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void drawFloatingStats()
    {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingTextView.setTextColor(Color.argb(255, 225, 100, 30));
        floatingTextView.setBackgroundColor(Color.argb(64, 200, 200, 200));
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
