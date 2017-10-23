package com.android.keivan.androidstats;

import android.annotation.TargetApi;
import android.content.Intent;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    public final static int REQUEST_CODE = 127;
    private boolean overlayPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        if(!overlayPermission) checkDrawOverlayPermission();
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
            Log.v("App", "Requesting Permission" + Settings.canDrawOverlays(this));
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getApplicationContext().getPackageName()));
            // request permission via start activity for result
            startActivityForResult(intent, REQUEST_CODE);
        }
        else
        {
            Log.v("App", "We already have permission for it.");
            Toast.makeText(getApplicationContext(), "WE HAVE Permission", Toast.LENGTH_LONG).show();
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
                    overlayPermission = true;
                    Toast.makeText(getApplicationContext(), "WE HAVE Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setOverlayPermission(boolean overlayPermission)
    {
        this.overlayPermission = overlayPermission;

    }
}
