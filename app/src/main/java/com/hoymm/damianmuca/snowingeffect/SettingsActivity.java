package com.hoymm.damianmuca.snowingeffect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {
    // WALLPAPER feature objects
    ImageView wallpaper_iv;
    int currentWallpaperIndex, wallpapersAmount;

    // OTHERS objects
    boolean useAccelerometerToDetect, snowflakeType1, snowflakeType2, snowflakeType3;
    CheckBox useAccelerometerToDetect_CB, snowflakeType1_CB, snowflakeType2_CB, snowflakeType3_CB;

    int snowflakesAmount, snowflakesSpeed;
    SeekBar snowflakesAmount_SB, snowflakesSpeed_SB;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Load settings from SharedPreferences, and set proper view over layout
        readSP();
        disableOrEnableWallpaperChoosing();
        initObjects();
        setReadedValuesFromSPToLayout();
    }

    private void disableOrEnableWallpaperChoosing() {
        Intent intent = getIntent();
        String ifEnableWallpaperChoosing = intent.getStringExtra(getString(R.string.EK_show_wallpaper_changing));

        // yes, enable changing wallpaper feature in settings
        if (ifEnableWallpaperChoosing.equals("yes")){
            initializateObjectsForWallpaperChoosingFeature();
        }
        // no, disable changing wallpaper feature in settings (HIDE IT)
        else{
            LinearLayout wallpaper_LL = (LinearLayout) findViewById(R.id.wallpaper_choosing_panel_id);
            wallpaper_LL.setVisibility(View.GONE);
        }

    }

    private void initializateObjectsForWallpaperChoosingFeature() {
        wallpapersAmount = 0;
        while(getResources().getIdentifier("wallpaper_"+wallpapersAmount, "raw", getPackageName())!=0)
            wallpapersAmount++;

        currentWallpaperIndex = sharedPref.getInt(getResources().getString(R.string.SP_current_wallpaper_index), 0);
        wallpaper_iv = (ImageView) findViewById(R.id.wallpaper_choosing_panel_iv_id);
        int wallpaper_raw_id = getResources().getIdentifier
                ("wallpaper_"+ currentWallpaperIndex, "raw", getPackageName());
        wallpaper_iv.setImageResource(wallpaper_raw_id);
    }


    private void setReadedValuesFromSPToLayout() {
        useAccelerometerToDetect_CB.setChecked(useAccelerometerToDetect);
        snowflakeType1_CB.setChecked(snowflakeType1);
        snowflakeType2_CB.setChecked(snowflakeType2);
        snowflakeType3_CB.setChecked(snowflakeType3);

        snowflakesAmount_SB.setProgress(snowflakesAmount);
        snowflakesAmount_SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                snowflakesAmount = seekBar.getProgress();
                seekBarSnoflakesAmountSaveToSP();
            }
        });

        snowflakesSpeed_SB.setProgress(snowflakesSpeed);
        snowflakesSpeed_SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                snowflakesSpeed = seekBar.getProgress();
                seekBarSnoflakesSpeedSaveToSP();
            }
        });

    }

    private void initObjects() {
        useAccelerometerToDetect_CB = (CheckBox) findViewById(R.id.use_accelerometer_id);

        snowflakeType1_CB = (CheckBox) findViewById(R.id.first_snowflake_cb_id);
        snowflakeType2_CB = (CheckBox) findViewById(R.id.second_snowflake_cb_id);
        snowflakeType3_CB = (CheckBox) findViewById(R.id.third_snowflake_cb_id);

        snowflakesAmount_SB = (SeekBar) findViewById(R.id.snowflakes_amount_id);
        snowflakesSpeed_SB = (SeekBar) findViewById(R.id.snowflakes_speed_id);

        snowflakesSpeed_SB.getProgress();
    }

    private void readSP() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        useAccelerometerToDetect =
                sharedPref.getBoolean(getResources().getString(R.string.SP_use_accelerometer_cb), true);
        snowflakeType1 =
                sharedPref.getBoolean(getResources().getString(R.string.SP_snowflakes_type_1_cb), true);
        snowflakeType2 =
                sharedPref.getBoolean(getResources().getString(R.string.SP_snowflakes_type_2_cb), true);
        snowflakeType3 =
                sharedPref.getBoolean(getResources().getString(R.string.SP_snowflakes_type_3_cb), true);

        snowflakesAmount =
                sharedPref.getInt(getResources().getString(R.string.SP_snowflakes_amount), StaticValues.getSnowflakesAmountByDefault());
        snowflakesSpeed =
                sharedPref.getInt(getResources().getString(R.string.SP_snowflakes_speed), StaticValues.getSnowflakesSpeedByDefault());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void accelometerToDetectSnowDirectionClicked(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.SP_use_accelerometer_cb), useAccelerometerToDetect_CB.isChecked());
        editor.apply();
    }

    public void snowflakesType1_CB(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        snowflakeType1_CB.setChecked(!snowflakeType1_CB.isChecked());
        editor.putBoolean(getString(R.string.SP_snowflakes_type_1_cb), snowflakeType1_CB.isChecked());
        editor.apply();
    }

    public void snowflakesType2_CB(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        snowflakeType2_CB.setChecked(!snowflakeType2_CB.isChecked());
        editor.putBoolean(getString(R.string.SP_snowflakes_type_2_cb), snowflakeType2_CB.isChecked());
        editor.apply();
    }

    public void snowflakesType3_CB(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        snowflakeType3_CB.setChecked(!snowflakeType3_CB.isChecked());
        editor.putBoolean(getString(R.string.SP_snowflakes_type_3_cb), snowflakeType3_CB.isChecked());
        editor.apply();
    }

    public void seekBarSnoflakesAmountSaveToSP() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.SP_snowflakes_amount), snowflakesAmount);
        editor.apply();
    }

    public void seekBarSnoflakesSpeedSaveToSP() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.SP_snowflakes_speed), snowflakesSpeed);
        editor.apply();
    }

    public void nextWallpaperClicked(View view) {
        changeDisplayingWallpaperOfIndex(1);
    }

    public void previousWallpaperClicked(View view) {
        changeDisplayingWallpaperOfIndex(-1);
    }


    private void changeDisplayingWallpaperOfIndex(int indexToChange) {
        currentWallpaperIndex = ((currentWallpaperIndex+indexToChange)%wallpapersAmount);

        // below is some mistake (-1)%2 should be 1, and program calculate it as -1, so we must handle it manually
        currentWallpaperIndex = currentWallpaperIndex < 0 ? wallpapersAmount-1 : currentWallpaperIndex;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.SP_current_wallpaper_index), currentWallpaperIndex);
        editor.apply();
        int wallpaper_raw_id = getResources().getIdentifier
                ("wallpaper_"+ currentWallpaperIndex, "raw", getPackageName());
        wallpaper_iv.setImageResource(wallpaper_raw_id);
    }
}
