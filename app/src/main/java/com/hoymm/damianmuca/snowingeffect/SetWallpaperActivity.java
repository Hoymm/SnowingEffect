package com.hoymm.damianmuca.snowingeffect;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SetWallpaperActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SnowGeneratorClass snowGenerator;

    // RUN PAUSE TIME
    private static final int RUN_TIME_PAUSE = 120;

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



        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // create object of snowflakesGeneratorClass, that will generate snowflakes over screen
        snowGenerator = new SnowGeneratorClass(
                this                                                // CONTEXT
                , getWindow().getDecorView().getRootView()          // VIEW
                , sharedPref.getInt(getResources().getString        // SNOWFLAKES AMOUNT
                (R.string.SP_snowflakes_amount), StaticValues.getSnowflakesAmountByDefault())
                , sharedPref.getInt(getResources().getString        // SNOWFLAKE SPEED
                (R.string.SP_snowflakes_speed), StaticValues.getSnowflakesSpeedByDefault())
                , sharedPref.getBoolean(getResources().getString    // if use ACCELEROMETR
                (R.string.SP_use_accelerometer_cb), StaticValues.isUseAccelerometrByDefault())
                , sharedPref.getBoolean(getResources().getString    // if use FIRST SNOWFLAKE
                (R.string.SP_snowflakes_type_1_cb), StaticValues.isUseFirstSnowflakeByDefault())
                , sharedPref.getBoolean(getResources().getString    // if use SECOND SNOWFLAKE
                (R.string.SP_snowflakes_type_2_cb), StaticValues.isUseSecondSnowflakeByDefault())
                , sharedPref.getBoolean(getResources().getString    // if use THIRD SNOWFLAKE
                (R.string.SP_snowflakes_type_3_cb), StaticValues.isUseThirdSnowflakeByDefault())
        );
        snowGenerator.onResume();


        super.onResume();
    }

    @Override
    protected void onPause() {
        snowGenerator.onPause();
        super.onPause();
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
        finish();
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
