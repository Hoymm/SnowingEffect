package com.hoymm.damianmuca.snowingeffect;

import android.app.WallpaperManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Intent settingsActivity = new Intent(this, SettingsActivity.class);
        settingsActivity.putExtra(getString(R.string.EK_show_wallpaper_changing), "no");
        startActivity(settingsActivity);
    }

    public void openSettingsActivity(View view) {
        Intent settingsActivity = new Intent(this, SettingsActivity.class);
        settingsActivity.putExtra(getString(R.string.EK_show_wallpaper_changing), "no");
        startActivity(settingsActivity);
    }

    public void openWallpaperActivity(View view) {
        Intent setWallpaperActivity = new Intent(this, SetWallpaperActivity.class);
        startActivity(setWallpaperActivity);
    }
}
