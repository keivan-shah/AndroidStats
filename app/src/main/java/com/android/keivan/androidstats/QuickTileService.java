package com.android.keivan.androidstats;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import java.util.Locale;

public class QuickTileService extends TileService
{

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onTileAdded() {
        Log.d("QS", "Tile added");
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        Log.d("QS", "Start listening");
    }

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        Log.d("QS", "Tile tapped");

        updateTile();
    }

    /**
     * Called when this tile moves out of the listening state.
     */
    @Override
    public void onStopListening() {
        Log.d("QS", "Stop Listening");
    }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        Log.d("QS", "Tile removed");
    }

    private void updateTile()
    {
        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isActive)
        {
            newLabel = getString(R.string.quick_tile_active_text);
            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.remove_stats);
            newState = Tile.STATE_ACTIVE;

        }
        else
        {
            newLabel = getString(R.string.quick_tile_inactive_text);
            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.add_stats);
            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isActive = prefs.getBoolean(getString(R.string.quick_tile_status), false);
        isActive = !isActive;
        prefs.edit().putBoolean(getString(R.string.quick_tile_status), isActive).apply();
        return isActive;
    }
}
