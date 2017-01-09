package com.hoymm.damianmuca.snowingeffect;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 09.01.17.
 */

public class SnowflakesGeneratorClass {
    // Objects initializated by constructor
    private Context myContext;
    private View view;
    private int snowflakesAmount, snowflakesFallingSpeed;
    private boolean useAccelerometrEnabled, isFirstSnowflakeActive, isSecondSnowflakeActive, isThirdSnowflakeActive;

    // other objects
    private float devWidth, devHeight;
    List<ImageView> myImageViewList;


    public SnowflakesGeneratorClass(Context context
            , View view
            , int snowflakesAmount
            , int snowflakesFallingSpeed
            ,boolean useAccelerometrEnabled
            ,boolean isFirstSnowflakeActive
            , boolean isSecondSnowflakeActive
            , boolean isThirdSnowflakeActive) {

        // ##### ASSIGN OBJECTS
        myContext = context;
        this.view = view;
        this.snowflakesAmount = snowflakesAmount;
        this.snowflakesFallingSpeed = snowflakesFallingSpeed;
        this.useAccelerometrEnabled = useAccelerometrEnabled;
        this.isFirstSnowflakeActive = isFirstSnowflakeActive;
        this.isSecondSnowflakeActive = isSecondSnowflakeActive;
        this.isThirdSnowflakeActive = isThirdSnowflakeActive;

        // ##### INIT OBJECTS
            // WIDTH and HEIGHT
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        devWidth = displayMetrics.widthPixels;
        devHeight = displayMetrics.heightPixels;
            // ImageView LIST
        myImageViewList = new ArrayList<>();


        initializateMyImageViewList();
        animationStart();
    }

    private void animationStart() {
        // use ACCELOMETER
        if (useAccelerometrEnabled)
            loadAccelometer();

        for (int i = 0 ; i < myImageViewList.size(); ++i) {

            AnimationSet myAnimation = new AnimationSet(true);

            myImageViewList.get(i).setVisibility(View.VISIBLE);
            //fallingAnimation.setFillAfter(true);
            myAnimation.addAnimation(generateFallingAnimation(i));

            myImageViewList.get(i).startAnimation(myAnimation);  // start fallingAnimation
        }
    }

    private Animation generateFallingAnimation(int i) {

        float startPos_X = (float) (Math.random() * (devWidth + 1))
                , startPos_Y = (float) (Math.random() * (devHeight + 1))/(1+(i%3));
        float endPos_X = startPos_X, endPos_Y = (int)devHeight;


        // spped radio counts DISTANCE to cover and multiply it by speed, so every snowflake has the same speed
        double speedRatio =
                Math.sqrt((Math.pow(startPos_X-endPos_X,2)+Math.pow(startPos_Y-endPos_Y,2))) / devHeight;
        int currentSnowflakeSpeed =
                (int)(snowflakesFallingSpeed*StaticValues.getSnowflakesSpeedMultipler()*speedRatio);



        TranslateAnimation fallingAnimation = new TranslateAnimation(startPos_X, endPos_X,
                startPos_Y, endPos_Y);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        fallingAnimation.setDuration(currentSnowflakeSpeed);  // fallingAnimation duration
        fallingAnimation.setRepeatCount(-1);  // fallingAnimation repeat count
        fallingAnimation.setRepeatMode(Animation.RESTART);   // repeat fallingAnimation (left to right, right to left )
        return fallingAnimation;
    }


    private void initializateMyImageViewList() {

        // generate LIST of snowflakes ImageViews
        int howManySnowflakesEnabled = 0;
        howManySnowflakesEnabled = isFirstSnowflakeActive ? howManySnowflakesEnabled+1 : howManySnowflakesEnabled;
        howManySnowflakesEnabled = isSecondSnowflakeActive ? howManySnowflakesEnabled+1 : howManySnowflakesEnabled;
        howManySnowflakesEnabled = isThirdSnowflakeActive ? howManySnowflakesEnabled+1 : howManySnowflakesEnabled;
        if (howManySnowflakesEnabled > 0) {
            for (int i = 0; i < snowflakesAmount / howManySnowflakesEnabled; i++) {
                for (int j = 1; j <= howManySnowflakesEnabled; ++j) {

                    RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.activity_set_wallpaper);
                    int curSnowflakeSizeRatio = StaticValues.getSnowflakesMinSize() + (int)(Math.random() *
                            ((StaticValues.getSnowflakesMaxSize() - StaticValues.getSnowflakesMinSize()) + 1));
                    int curSnowflakeSize = (int) (devWidth < devHeight ? devWidth : devHeight)
                            /100*curSnowflakeSizeRatio;

                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(curSnowflakeSize,curSnowflakeSize);

                    ImageView temp_iv = new ImageView(myContext);
                    temp_iv.setLayoutParams(param);
                    switch(j) {
                        case 1:
                            temp_iv.setImageResource(R.drawable.menu_snow_1);
                            break;
                        case 2:
                            temp_iv.setImageResource(R.drawable.menu_snow_2);
                            break;
                        case 3:
                            temp_iv.setImageResource(R.drawable.menu_snow_3);
                            break;
                    }
                    temp_iv.setVisibility(View.GONE);
                    // Adds the view to the layout
                    layout.addView(temp_iv);
                    myImageViewList.add(temp_iv);
                }
            }
        }
    }

    private void loadAccelometer() {

    }
}
