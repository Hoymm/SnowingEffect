package com.hoymm.damianmuca.snowingeffect;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ParticleModifier;

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
    private float devWidth, devHeight, density;
    private List<ParticleSystem> mySnowflakesFromTop_PL;
    private List <Boolean> isEmittingCurrently;
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

    public SnowGeneratorClass(
            Context context
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
        this.snowflakesFallingSpeed = snowflakesFallingSpeed+10;
        this.useAccelerometrEnabled = useAccelerometrEnabled;
        this.isFirstSnowflakeActive = isFirstSnowflakeActive;
        this.isSecondSnowflakeActive = isSecondSnowflakeActive;
        this.isThirdSnowflakeActive = isThirdSnowflakeActive;

        // ##### INIT OBJECTS
        // WIDTH and HEIGHT
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        devWidth = displayMetrics.widthPixels;
        devHeight = displayMetrics.heightPixels;
        density = displayMetrics.density;


    }

    private void generateSnowing() {
        mySnowflakesFromTop_PL = new ArrayList<>();
        isEmittingCurrently = new ArrayList<>();

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
            isEmittingCurrently.add(false);
        }
    }
    int timeToDie;
    private ParticleSystem generateParticleSnowfallObject(int drawable_id) {

        Bitmap bMap = BitmapFactory.decodeResource(myContext.getResources(), drawable_id);
        int newSnowflakeSize = getRandomDrawableSize();
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, newSnowflakeSize, newSnowflakeSize, true);


        timeToDie = (int)((Math.sqrt(Math.pow(devWidth,2)+Math.pow(devHeight,2)) * density));
                //(StaticValues.getSnowflakesSpeedMultiplerMin() * snowflakesFallingSpeed * 23));
                //(StaticValues.getSnowflakesSpeedMultiplerMin() * snowflakesFallingSpeed);
        float speed = StaticValues.getSnowflakesSpeedMultiplerMin() * snowflakesFallingSpeed * 100;
        float amountDivider = timeToDie / (StaticValues.getSnowflakesSpeedMultiplerMin() * 148);
        timeToDie = (int)(timeToDie/(StaticValues.getSnowflakesSpeedMultiplerMin() * snowflakesFallingSpeed * 4));

        // Alpha fading in modifier
        ParticleModifier fadingInModifier = new AlphaModifier(0, 255, 0, 2000);


        ParticleSystem resultPS = new ParticleSystem
                ((Activity) myContext, timeToDie, bMapScaled, timeToDie)
                .setSpeedModuleAndAngleRange(
                        StaticValues.getSnowflakesSpeedMultiplerMin() * snowflakesFallingSpeed
                        , StaticValues.getSnowflakesSpeedMultiplerMax() * snowflakesFallingSpeed
                        , StaticValues.getWindDegreesMin()
                        , StaticValues.getWindDegreesMax())
                .setRotationSpeed(speed*(StaticValues.getSnowflakesRotationMinSpeed() +
                        (int) (Math.random() * ((StaticValues.getSnowflakesRotationMaxSpeed() -
                                StaticValues.getSnowflakesRotationMinSpeed()) + 1))))
                /*.setAcceleration(StaticValues.getWindIntensityMin() + (int) (Math.random() *
                                ((StaticValues.getWindIntensityMax() - StaticValues.getWindIntensityMin()) + 1)) *
                                snowflakesFallingSpeed
                        , StaticValues.getWindDegreesMin() + (int) (Math.random() *
                                ((StaticValues.getWindDegreesMax() -
                                        StaticValues.getWindDegreesMin()) + 1)))*/
                //.addModifier(fadingInModifier)
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

        curIndex = 0;
        // RUN function objects resume

        generateSnowing();
        myThread = new Thread(this);
        myThread.start();
    }

    void onPause(){
        // RUN function objects pause
        isThatOk = false;
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myThread = null;


    }

    private ArrayList<Integer> myRandomIndexesSnowflakesFromRoof;
    private long lastTimeSinceStartedEmitting = 0;

    @Override
    public void run() {
       while (isThatOk) {
            // initialization
            try {
                if (curIndex < mySnowflakesFromTop_PL.size()
                        && (System.currentTimeMillis()-lastTimeSinceStartedEmitting) > 30000/snowflakesAmount
                        ) {
                    if (curIndex == 0)
                        initRandomListIndexesFromRoofSnowflakes();
                    mySnowflakesFromTop_PL.get(curIndex);

                    final int randomIndex = getRandomIndexSnowflakesRoof();
                    int lastStartedEmittingIndex = randomIndex;
                    synchronized (this) {
                        ((Activity) myContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mySnowflakesFromTop_PL.get(randomIndex)
                                        .emit((int) (Math.random() * devWidth)
                                                , -50
                                                , 1);

                                isEmittingCurrently.set(randomIndex,true);
                            }
                        });
                    }
                    lastTimeSinceStartedEmitting = System.currentTimeMillis();
                    lastStartedEmittingIndex = randomIndex;
                    curIndex++;
                    Thread.sleep(300);
                    mySnowflakesFromTop_PL.get(lastStartedEmittingIndex).stopEmitting();
                    isEmittingCurrently.set(lastStartedEmittingIndex, false);
                }
                Thread.sleep(40);
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
