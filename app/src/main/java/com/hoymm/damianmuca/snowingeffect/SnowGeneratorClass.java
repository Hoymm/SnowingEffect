package com.hoymm.damianmuca.snowingeffect;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10.01.17.
 */

public class SnowGeneratorClass implements Runnable{

    // Objects initializated by constructor
    private Context myContext;
    private View view;
    private int snowflakesAmount, snowflakesFallingSpeed;
    private boolean useAccelerometrEnabled, isFirstSnowflakeActive, isSecondSnowflakeActive, isThirdSnowflakeActive;


    // other objects
    private float devWidth, devHeight;
    private List<ParticleSystem> mySnowflakesFromTop_PL;
    private int howManyTypesOfSnowflakes = 0;

    // implements runnable objects
    private Thread myThread = null;
    private boolean isThatOk = true;
    private static final int RUN_TIME_PAUSE = 15;
    private int curIndex = 0;

    public List<ParticleSystem> getMySnowflakesFromTop_PL() {
        return mySnowflakesFromTop_PL;
    }

    public int getHowManyTypesOfSnowflakes() {
        return howManyTypesOfSnowflakes;
    }

    public SnowGeneratorClass(Context context
            , View view
            , int snowflakesAmount
            , int snowflakesFallingSpeed
            , boolean useAccelerometrEnabled
            , boolean isFirstSnowflakeActive
            , boolean isSecondSnowflakeActive
            , boolean isThirdSnowflakeActive
    ) {

        // ##### ASSIGN OBJECTS
        myContext = context;
        this.view = view;
        this.snowflakesAmount = snowflakesAmount/3 == 0 ? 3 : snowflakesAmount-(snowflakesAmount%3);
        this.snowflakesFallingSpeed = snowflakesFallingSpeed+1;
        this.useAccelerometrEnabled = useAccelerometrEnabled;
        this.isFirstSnowflakeActive = isFirstSnowflakeActive;
        this.isSecondSnowflakeActive = isSecondSnowflakeActive;
        this.isThirdSnowflakeActive = isThirdSnowflakeActive;

        // ##### INIT OBJECTS
        // WIDTH and HEIGHT
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        devWidth = displayMetrics.widthPixels;
        devHeight = displayMetrics.heightPixels;


    }

    private void generateSnowing() {
        mySnowflakesFromTop_PL = new ArrayList<>();

        howManyTypesOfSnowflakes = isFirstSnowflakeActive ? howManyTypesOfSnowflakes+1 : howManyTypesOfSnowflakes;
        howManyTypesOfSnowflakes = isSecondSnowflakeActive ? howManyTypesOfSnowflakes+1 : howManyTypesOfSnowflakes;
        howManyTypesOfSnowflakes = isThirdSnowflakeActive ? howManyTypesOfSnowflakes+1 : howManyTypesOfSnowflakes;

        if (isFirstSnowflakeActive)
            generateSnowflakesFromTop(R.drawable.menu_snow_1);
        if (isSecondSnowflakeActive)
            generateSnowflakesFromTop(R.drawable.menu_snow_2);
        if (isThirdSnowflakeActive)
            generateSnowflakesFromTop(R.drawable.menu_snow_3);
    }

    public int getSnowflakesAmount() {
        return snowflakesAmount;
    }



    public float getDevHeight() {

        return devHeight;
    }

    public float getDevWidth() {
        return devWidth;
    }

    private void generateSnowflakesFromTop(int drawable_id) {

        for (int i = 0; i < snowflakesAmount/howManyTypesOfSnowflakes; ++i) {
            mySnowflakesFromTop_PL.add(generateParticleSnowfallObject(drawable_id));
        }
    }

    private ParticleSystem generateParticleSnowfallObject(int drawable_id) {
        Drawable myCurSnowDrawable = ContextCompat.getDrawable(myContext, drawable_id);
        Bitmap bitmap = ((BitmapDrawable) myCurSnowDrawable).getBitmap();
        int newSnowflakeSize = getRandomDrawableSize();
        myCurSnowDrawable = new BitmapDrawable
                (myContext.getResources(), Bitmap.createScaledBitmap(bitmap, newSnowflakeSize, newSnowflakeSize, true));

        Interpolator interpolator = new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        };

        ParticleSystem resultPS = new ParticleSystem((Activity) myContext, snowflakesAmount, myCurSnowDrawable, 10000)
                .setSpeedModuleAndAngleRange(StaticValues.getSnowflakesSpeedMultiplerMin() * snowflakesFallingSpeed
                        , StaticValues.getSnowflakesSpeedMultiplerMax() * snowflakesFallingSpeed
                        , StaticValues.getWindDegreesMin(), StaticValues.getWindDegreesMax())
                .setRotationSpeed(StaticValues.getSnowflakesRotationMinSpeed() +
                        (int) (Math.random() * ((StaticValues.getSnowflakesRotationMaxSpeed() -
                                StaticValues.getSnowflakesRotationMinSpeed()) + 1)))
                .setAcceleration(StaticValues.getWindIntensityMin() + (int) (Math.random() *
                                ((StaticValues.getWindIntensityMax() - StaticValues.getWindIntensityMin()) + 1)) *
                                snowflakesFallingSpeed
                        , StaticValues.getWindDegreesMin() + (int) (Math.random() *
                                ((StaticValues.getWindDegreesMax() -
                                        StaticValues.getWindDegreesMin()) + 1)))
                .setFadeOut(2000, interpolator)
                ;
        return resultPS;
    }

    private void animationStart() {
        // use ACCELOMETER
        if (useAccelerometrEnabled)
            loadAccelometer();

    }


    private int getRandomDrawableSize() {
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.activity_set_wallpaper);
        int curSnowflakeSizeRatio = StaticValues.getSnowflakesMinSize() + (int)(Math.random() *
                ((StaticValues.getSnowflakesMaxSize() - StaticValues.getSnowflakesMinSize()) + 1));
        int curSnowflakeSize = (int) (devWidth < devHeight ? devWidth : devHeight)
                /100*curSnowflakeSizeRatio;
        return curSnowflakeSize;
    }

    private void loadAccelometer() {

    }

    public void onResume(){
        isThatOk = true;

        generateSnowing();
        curIndex = 0;
        // RUN function objects resume
        myThread = new Thread(this);
        myThread.start();
    }

    void onPause(){
        // RUN function objects pause
        isThatOk = false;
        for (int i = 0 ; i < mySnowflakesFromTop_PL.size() ; ++i){
            mySnowflakesFromTop_PL.get(i).stopEmitting();
        }
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myThread = null;


    }

    ArrayList<Integer> myRandomIndexesSnowflakesFromRoof;
    @Override
    public void run() {
        while (isThatOk) {
            // initialization
            try {
                if (curIndex < mySnowflakesFromTop_PL.size()) {
                    if (curIndex == 0)
                        initRandomListIndexesFromRoofSnowflakes();
                    mySnowflakesFromTop_PL.get(curIndex);

                    final int randomIndex = getRandomIndexSnowflakesRoof();
                    final float spaceBetweenSnowflakes = devWidth / (snowflakesAmount / this.getHowManyTypesOfSnowflakes());
                    final int emitX = (int) spaceBetweenSnowflakes *
                            (randomIndex % snowflakesAmount / howManyTypesOfSnowflakes);

                    synchronized (this) {
                        ((Activity) myContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mySnowflakesFromTop_PL.get(randomIndex)
                                        .emit(emitX + (int) (-spaceBetweenSnowflakes / 2 + (Math.random() *
                                                        (spaceBetweenSnowflakes / 2 + spaceBetweenSnowflakes / 2)))
                                                , -50
                                                , 1);
                            }
                        });
                    }
                    curIndex++;
                }
                // UPDATE EMIT POINT
                else {
                    mySnowflakesFromTop_PL.get((int)(Math.random()*mySnowflakesFromTop_PL.size()))
                            .updateEmitPoint((int)(Math.random()*devWidth)
                                    , -50);
                }
                Thread.sleep(900/mySnowflakesFromTop_PL.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int getRandomIndexSnowflakesRoof() {
        int indexRandom = (int) (Math.random() * myRandomIndexesSnowflakesFromRoof.size());
        int resultValue = myRandomIndexesSnowflakesFromRoof.get(indexRandom);
        myRandomIndexesSnowflakesFromRoof.remove(indexRandom);
        return resultValue;
    }

    private void initRandomListIndexesFromRoofSnowflakes() {
        myRandomIndexesSnowflakesFromRoof = new ArrayList<>();
        for (int i = 0; i < mySnowflakesFromTop_PL.size(); ++i){
            myRandomIndexesSnowflakesFromRoof.add(i);
        }
    }
}
