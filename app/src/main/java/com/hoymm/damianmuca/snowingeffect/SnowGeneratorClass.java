package com.hoymm.damianmuca.snowingeffect;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by root on 10.01.17.
 */

class SnowGeneratorClass implements Runnable, SensorEventListener {

    // Objects initializated by constructor
    private Context myContext;
    final private View view;
    private int snowflakesAmount, snowflakesFallingSpeed;
    private boolean useAccelerometrEnabled, isFirstSnowflakeActive
            , isSecondSnowflakeActive, isThirdSnowflakeActive;

    SharedPreferences sharedPref;
    private ArrayList<Snowflake> mySnowflakesAL;

    // other objects
    private float devWidth, devHeight, density, destinationPointX, destinationPointY;
    // adjustDestinationAngle calculate random destination angle for current snowflake, then its is adding to the ground destination
    // and imitates little wind, i.e. ground has 140 degrees then, we add value of adjustDestinationAngle (i.e. -9 degree),
    // and we get designation angle eqaul to 131 degrees
    private int adjustDestinationAngle =  StaticValues.getFallingAngleVariationMin()
            + (int)(Math.random()*(StaticValues.getFallingAngleVariationMax()-StaticValues.getFallingAngleVariationMin())+1);

    private double radius;
    private int howManyTypesOfSnowflakes = 0;
    private RelativeLayout mainRelativeLayout;
    private int myDegrees = 0;

    // implements runnable objects
    private Thread myThread = null;
    private boolean isThatOk = true;
    public SnowGeneratorClass(
            Context context
            , final View view
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

        // get SharedPreferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(myContext);

        // assing arguments
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
        radius = (Math.sqrt(Math.pow(devWidth/2,2) + Math.pow(devHeight/2,2))+100);
        density = displayMetrics.density;

        // Initializate accelerometer
        if (useAccelerometrEnabled)
            initAccelerometer();

        mainRelativeLayout = (RelativeLayout) view.findViewById(R.id.activity_set_wallpaper);

        // init snowflakes by id drawables
        mySnowflakesAL = new ArrayList<>();
    }

    boolean sensorChecked = false;
    private void initAccelerometer() {
        // Check orientation for changing direction of smoke rising
        OrientationEventListener myOrientationEventListener = new OrientationEventListener(myContext, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int currentDegrees) {
                // TODO Auto-generated method stub
                int naturalOrientation =
                        ((WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE))
                                .getDefaultDisplay().getRotation();

                // ADJUST FOR OTHER DEVICES, some devices have other accelerometer values then others,
                // we adjust it
                if (naturalOrientation == Surface.ROTATION_0) {
                    Log.d("Rotation ROTATION: ", "0");
                } else if (naturalOrientation == Surface.ROTATION_90) {
                    Log.d("Rotation ROTATION: ", "90");
                    currentDegrees += 90;
                } else if (naturalOrientation == Surface.ROTATION_180) {
                    Log.d("Rotation ROTATION: ", "180");
                    currentDegrees += 180;
                } else if (naturalOrientation == Surface.ROTATION_270) {
                    Log.d("Rotation ROTATION: ", "270");
                    currentDegrees += 270;
                }
                // Check if we have gone too far forward with rotation adjustment, keep the result
                // between 0-360
                if (currentDegrees > 360) {
                    currentDegrees -= 360;
                }

                myDegrees = currentDegrees; // goes from left to top (9 hour clockwise) 0 degree to 360
                TextView tempSensor = (TextView) view.findViewById(R.id.temp_sensor_tv_id);
                tempSensor.setText(String.valueOf(myDegrees));
                sensorChecked = true;
            }
        };
        if (myOrientationEventListener.canDetectOrientation()) {
            //Toast.makeText(this, "Can DetectOrientation", Toast.LENGTH_LONG).show();
            myOrientationEventListener.enable();
        } else {
            Toast.makeText(myContext, "Can't DetectOrientation", Toast.LENGTH_LONG).show();
        }
    }

    // Snowflake class create snowflake animation and controls it
    private class Snowflake {
        private long startedTime;
        private short fallingAngleVariation;

        // snowflake falling speed is expressed by distance of resolution to cover over cycle animation (i.e. 1000 miliseconds)
        private float snowflakeFallingSpeed;
        private ImageView mySnowflakeIV;
        AnimatorSet animatorSet;
        private boolean beginHidingSnowflakeBeforeDie, beginShowingSnowflakeAgain;
        private int destinationRadius;
        public Snowflake(int drawableId) {

            // initialization
            mySnowflakeIV = new ImageView(myContext);
            mySnowflakeIV.setImageDrawable(ContextCompat.getDrawable(myContext,drawableId));
            beginHidingSnowflakeBeforeDie = false;
            beginShowingSnowflakeAgain = true;

            // drawable size
            int snowflakeWidth = getRandomDrawableSize();
            mySnowflakeIV.setLayoutParams(new RelativeLayout.LayoutParams(snowflakeWidth,snowflakeWidth));
            fallingAngleVariation = (short)(StaticValues.getFallingAngleVariationMin() +
                    (Math.random()*(StaticValues.getFallingAngleVariationMax()-StaticValues.getFallingAngleVariationMin())+1));

            // set new position of drawable
            generateNewStartPosition();

            // calculate falling speed for current snowflake
            calculateFallingSpeedForCurrentSnowflake();

            // add drawable to screen
            mainRelativeLayout.addView(mySnowflakeIV);

            // create animation
            mySnowflakeIV.setAlpha(0f);
            animatorSet = createAnimation(true);
            startedTime = System.currentTimeMillis();

            // destination point radius must be beyond RECTANGLE, and little beyond circle described on the rectangle
            destinationRadius = (int)(radius * 1.15);
        }

        private void calculateFallingSpeedForCurrentSnowflake() {
            // get random multiper
            double snowflakeRandomMultipler = StaticValues.getSnowflakesSpeedMultiplerMin()
                    + (Math.random()*(StaticValues.getSnowflakesSpeedMultiplerMax()-StaticValues.getSnowflakesSpeedMultiplerMin()));
            // read settings speed (configured by user)
            int speedConfiguredInAppSettings =  sharedPref.getInt(myContext.getResources().getString(R.string.SP_snowflakes_speed)
                    , StaticValues.getSnowflakesSpeedByDefault());
            snowflakeFallingSpeed = (float)(snowflakeRandomMultipler * speedConfiguredInAppSettings);
        }

        void onPause(){
            mySnowflakeIV.setImageDrawable(null);
        }

        private void generateNewStartPosition() {
            // prevent myDegrees to be 0, (because 0 and 360 is the same number)
            myDegrees = myDegrees == 0 ? 360 : myDegrees;
            // generate random angle (myDegrees-90;myDegrees+90)
            int randomAngle = (int) ((myDegrees-90) + Math.random()*(180)+1);
            int randomRadius =  1 + (int)(Math.random()*(radius-1)+1);
            randomRadius /= 3;

            mySnowflakeIV.setX(devWidth/2 - (float)(Math.sin(Math.toRadians(randomAngle)) * randomRadius));
            mySnowflakeIV.setY(devHeight/2 - (float)(Math.cos(Math.toRadians(randomAngle)) * randomRadius));

           /* Log.e("ANGLE ADJUST: ","|| myDegrees: " + myDegrees + "\t\t|| randomAngle: " + randomAngle + "\t\t|| 360-randomAngle: "
                    + (360-randomAngle) + "\t\t|| (360-randomAngle)%90: "
                    + (360-randomAngle)%90 + "\t ||X: " + result.X + "\t||Y: " + result.Y);*/
        }

        void startAnimation(){
            animatorSet.start();
        }

        private AnimatorSet createAnimation(boolean animationNewlyStarted) {
            AnimatorSet resultAnimatorSet = new AnimatorSet();
            resultAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // When animation is to die then hide completely snowflake before it's death
                    if (startedTime+15000L < System.currentTimeMillis()
                            || mySnowflakeIV.getX() > radius * 1.1 || mySnowflakeIV.getX() < 0 - radius * 1.1
                            || mySnowflakeIV.getY() > radius * 1.1 || mySnowflakeIV.getY() < 0 - radius * 1.1) {
                        beginHidingSnowflakeBeforeDie = true; // means that you hide it, then change position
                        beginShowingSnowflakeAgain = true;  // ... and show it
                        startedTime = System.currentTimeMillis();
                    }
                    createAnimation(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            // first start is synchronized from main run thread, next are invoked recursively
            generateNewRandomDestinationPoint();
            if (beginHidingSnowflakeBeforeDie){
                resultAnimatorSet
                        .play(new_XorY_DestinationParameters(true))     // - true == X parameter
                        .with(new_XorY_DestinationParameters(false))     // - false == X parameter
                        .with(rotationAnimation())
                        .with(alphaShowAnimation(false));
                if (mySnowflakeIV.getAlpha() < StaticValues.getSnowflakesAlphaDuration())
                    beginHidingSnowflakeBeforeDie = false;
            }
            else {
                resultAnimatorSet
                        .play(new_XorY_DestinationParameters(true))     // - true == X parameter
                        .with(new_XorY_DestinationParameters(false))     // - false == X parameter
                        .with(rotationAnimation())
                        .with(alphaShowAnimation(true));
            }
            // animation refresh (heading direction)
            resultAnimatorSet.setDuration(StaticValues.getAnimReactionDelay());

            // if we start our animation newly, do not invoke method below
            if (!animationNewlyStarted)
                resultAnimatorSet.start();


            return resultAnimatorSet;
        }

        private void generateNewRandomDestinationPoint() {
            float groundDegreeses = (myDegrees+adjustDestinationAngle+180)%360;
            // prevent groundDegreeses to be 0, (because 0 and 360 is the same number)
            groundDegreeses = groundDegreeses == 0 ? 360 : groundDegreeses;

            destinationPointX = devWidth/2 - (float)(Math.sin(Math.toRadians(groundDegreeses)) * destinationRadius);
            destinationPointY = devHeight/2 - (float)(Math.cos(Math.toRadians(groundDegreeses)) * destinationRadius);
        }

        private Animator alphaShowAnimation(boolean showingEffect) {
            ValueAnimator alphaAnimation;

            // if showing alpha then addition, otherwise substraction
            float alphaChange = showingEffect
                    ? StaticValues.getSnowflakesAlphaDuration() : - StaticValues.getSnowflakesAlphaDuration();

            alphaAnimation = ValueAnimator.ofFloat(mySnowflakeIV.getAlpha(), mySnowflakeIV.getAlpha()+alphaChange);
            alphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mySnowflakeIV.setAlpha(value);
                }
            });
            alphaAnimation.setRepeatCount(0);
            return alphaAnimation;
        }

        private Animator rotationAnimation() {
            ValueAnimator rotation;
            rotation = ValueAnimator.ofFloat(mySnowflakeIV.getRotation()%360
                    , (mySnowflakeIV.getRotation()%360)+36);
            rotation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mySnowflakeIV.setRotation(value);
                }
            });
            rotation.setRepeatCount(0);
            rotation.setInterpolator(new LinearInterpolator());
            return rotation;
        }

        private ValueAnimator new_XorY_DestinationParameters(final boolean X_Axis) {

            ValueAnimator result_YorX_PosChange;
            float XDestination, YDestination;
            double totalDistanceToCover = Math.sqrt(Math.pow(mySnowflakeIV.getX()-destinationPointX,2)
                    +Math.pow(mySnowflakeIV.getY()-destinationPointY,2));
            double currentDistanceToCover = snowflakeFallingSpeed;
            double alphaAngle = mySnowflakeIV.getY() - destinationPointY

            //1
            if (X_Axis) {
                result_YorX_PosChange = ValueAnimator.ofFloat(mySnowflakeIV.getX()
                        , mySnowflakeIV.getX() - (5 * density));
            }
            else {
                result_YorX_PosChange = ValueAnimator.ofFloat(mySnowflakeIV.getY()
                        , (float) (mySnowflakeIV.getY() - density * (Math.sin(Math.toRadians(2 * 6)) * 12)));
            }
            //2
            result_YorX_PosChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //3
                    float value = (float) animation.getAnimatedValue();
                    //4
                    if (X_Axis)
                        mySnowflakeIV.setTranslationX(value);
                    else
                        mySnowflakeIV.setTranslationY(value);
                }

            });

            result_YorX_PosChange.setRepeatCount(0);
            result_YorX_PosChange.setInterpolator(new LinearInterpolator());

            return result_YorX_PosChange;
        }
    }


    private void generateSnowing() {
        howManyTypesOfSnowflakes = isFirstSnowflakeActive ? howManyTypesOfSnowflakes+1 : howManyTypesOfSnowflakes;
        howManyTypesOfSnowflakes = isSecondSnowflakeActive ? howManyTypesOfSnowflakes+1 : howManyTypesOfSnowflakes;
        howManyTypesOfSnowflakes = isThirdSnowflakeActive ? howManyTypesOfSnowflakes+1 : howManyTypesOfSnowflakes;

    }

    private int getRandomDrawableSize() {
        int curSnowflakeSizeRatio = StaticValues.getSnowflakesMinSize() + (int)(Math.random() *
                ((StaticValues.getSnowflakesMaxSize() - StaticValues.getSnowflakesMinSize()) + 1));
        return (int) (devWidth < devHeight ? devWidth : devHeight)
                /100*curSnowflakeSizeRatio;
    }

    void onResume(){
        isThatOk = true;
        // RUN function objects resume

        generateSnowing();
        myThread = new Thread(this);
        myThread.start();
    }

    void onPause(){
        for (int i = 0 ; i < mySnowflakesAL.size(); ++i){
            mySnowflakesAL.get(i).onPause();
            mySnowflakesAL.remove(i);
        }
        // RUN function objects pause
        isThatOk = false;
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myThread = null;


    }

    private int animationList = 0, snowflakesInitIndex = 0;
    @Override
    public void run() {
        while (isThatOk) {

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // when accelerometer does not work yet, skip code
            if (!sensorChecked){
                continue;
            }





                // run next animation
                ((Activity) myContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // if no snoflake type enabled, then skip it
                        if (isFirstSnowflakeActive || isSecondSnowflakeActive || isThirdSnowflakeActive)
                            if (snowflakesInitIndex < snowflakesAmount) {
                                if (isFirstSnowflakeActive) {
                                    mySnowflakesAL.add(new Snowflake(R.drawable.menu_snow_1));
                                    ++snowflakesInitIndex;
                                }
                                if (isSecondSnowflakeActive) {
                                    mySnowflakesAL.add(new Snowflake(R.drawable.menu_snow_2));
                                    ++snowflakesInitIndex;
                                }
                                if (isThirdSnowflakeActive) {
                                    mySnowflakesAL.add(new Snowflake(R.drawable.menu_snow_3));
                                    ++snowflakesInitIndex;
                                }
                            }


                        if (animationList < mySnowflakesAL.size())
                            mySnowflakesAL.get(animationList++).startAnimation();
                    }
                });
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
