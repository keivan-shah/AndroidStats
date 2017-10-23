package com.android.keivan.androidstats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity
{


    public static final String OVERLAY_BACKGROUND_COLOR = "OVERLAY_BACKGROUND_COLOR";
    public static final String OVERLAY_TEXT_COLOR = "OVERLAY_TEXT_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
