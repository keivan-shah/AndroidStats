package com.android.keivan.androidstats;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    public final static int REQUEST_CODE = 127;
    private boolean overlayPermission;
    static ImageButton startButton;
    public static boolean overlayDrawn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("Pref",pref.getAll().toString());

        if(!overlayPermission) checkDrawOverlayPermission();

        startButton = (ImageButton) findViewById(R.id.startButton);

        if(!overlayPermission)
        {
            startButton.setEnabled(false);
        }
        else
        {
            startButton.setEnabled(true);
            if(overlayDrawn)
            {
                startButton.setSelected(true);
            }
            else
            {
                startButton.setSelected(false);
            }
        }
        startButton.setBackgroundResource(R.drawable.selector);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View button)
            {
                if (button.isSelected())
                {
                    Log.d("Button","Setting to false");
                    startButton.setSelected(false);
                    startButton.setBackgroundResource(R.drawable.selector);
                    stopService(new Intent(MainActivity.this, StatsService.class));
                }
                else
                {
                    Log.d("Button","Setting to true");
                    startButton.setSelected(true);
                    startButton.setBackgroundResource(R.drawable.selector);
                    startService(new Intent(MainActivity.this, StatsService.class));

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission()
    {
        Log.v("App", "Package Name: " + getApplicationContext().getPackageName());

        // check if we already  have permission to draw over other apps
        if(!Settings.canDrawOverlays(this))
        {
            setOverlayPermission(false);
            Log.v("App", "Requesting Permission" + Settings.canDrawOverlays(this));
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getApplicationContext().getPackageName()));
            // request permission via start activity for result
            Toast.makeText(getApplicationContext(), "Please Grant This Permission!", Toast.LENGTH_LONG).show();
            startActivityForResult(intent, REQUEST_CODE);
        }
        else
        {
            setOverlayPermission(true);
            Log.v("App", "App already has required permissions.");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.v("App", "OnActivity Result.");
        //  check if received result code
        //  is equal our requested code for draw permission
        if(requestCode == REQUEST_CODE)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(Settings.canDrawOverlays(this))
                {
                    startButton.setEnabled(true);
                    setOverlayPermission(true);
                    Toast.makeText(getApplicationContext(), "App has required Permission!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setOverlayPermission(boolean overlayPermission)
    {
        this.overlayPermission = overlayPermission;
        StatsService.setOverlayPermission(overlayPermission);
    }

    public static void overlayDestroy()
    {
        overlayDrawn=false;
        startButton.setSelected(false);
    }
}
