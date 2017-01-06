package com.hoymm.damianmuca.snowingeffect;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SetWallpaperActivity extends AppCompatActivity {
    SharedPreferences sharedPref;

    ImageView currentWallpaperIV;
    int wallpaper_drawable_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);
    }

    @Override
    protected void onResume() {
        // read SharedPreferences, and refresh ImageView
        readSPAndRefreshIV();
        super.onResume();
    }

    private void readSPAndRefreshIV() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int index = sharedPref.getInt(getResources().getString(R.string.SP_current_wallpaper_index), 0);
        wallpaper_drawable_id = getResources().getIdentifier("wallpaper_"+ index, "raw", getPackageName());


        currentWallpaperIV = (ImageView) findViewById(R.id.my_cur_wallpaper_iv_id);
        currentWallpaperIV.setImageDrawable(ContextCompat.getDrawable(this, wallpaper_drawable_id));
    }

    public void openSettingsWithWallpaperButtonClicked(View view) {
        Intent settingsActivity = new Intent(this, SettingsActivity.class);
        settingsActivity.putExtra(getString(R.string.EK_show_wallpaper_changing), "yes");
        startActivity(settingsActivity);
    }

    public void setThisWallpaperButtonClicked(View view) {
        setWallpaper();
    }

    private void setWallpaper() {
        // TODO Auto-generated method stub
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setResource(wallpaper_drawable_id);
            //myWallpaperManager.setWallpaperOffsetSteps(1, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error when loading wallpaper", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
