package com.hoymm.damianmuca.snowingeffect;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ParticleModifier;

import java.util.ArrayList;

/**
 * Created by root on 10.01.17.
 */

public class SnowGeneratorClass implements Runnable, SensorEventListener {

    // Objects initializated by constructor
    private Context myContext;
    final private View view;
    private int snowflakesAmount, snowflakesFallingSpeed;
    private boolean useAccelerometrEnabled, isFirstSnowflakeActive
            , isSecondSnowflakeActive, isThirdSnowflakeActive;

    private ArrayList<Snowflake> mySnowflakesAL;

    // other objects
    private float devWidth, devHeight, density, centerX, centerY;
    private double radius;
    private int howManyTypesOfSnowflakes = 0;
    RelativeLayout mainRelativeLayout;
    // accelerometer
    OrientationEventListener myOrientationEventListener;
    private int myDegrees = 0;


    private int tempImageSize = 50;

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
        radius = Math.sqrt(Math.pow(devWidth/2,2) + Math.pow(devHeight/2,2))+tempImageSize;
        centerX =  devWidth/2;
        centerY = devHeight/2;
        density = displayMetrics.density;

        // Initializate accelerometer
        if (useAccelerometrEnabled)
            initAccelerometer();

        mainRelativeLayout = (RelativeLayout) view.findViewById(R.id.activity_set_wallpaper);

        // init snowflakes
        mySnowflakesAL = new ArrayList<>();
        for (int i = 0 ; i < this.snowflakesAmount ; ++i) {
            mySnowflakesAL.add(new Snowflake());
        }
    }


    private void initAccelerometer() {
        // Check orientation for changing direction of smoke rising
        myOrientationEventListener
                = new OrientationEventListener(myContext, SensorManager.SENSOR_DELAY_NORMAL) {
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

                myDegrees = currentDegrees;
                currentDegrees = 360 - currentDegrees;
                TextView tempSensor = (TextView) view.findViewById(R.id.temp_sensor_tv_id);
                tempSensor.setText(String.valueOf(currentDegrees));
            }
        };
        if (myOrientationEventListener.canDetectOrientation()) {
            //Toast.makeText(this, "Can DetectOrientation", Toast.LENGTH_LONG).show();
            myOrientationEventListener.enable();
        } else {
            Toast.makeText(myContext, "Can't DetectOrientation", Toast.LENGTH_LONG).show();
        }
    }
    private class Snowflake {
        private long startedTime;
        private ImageView mySnowflakeIV;
        AnimatorSet animatorSet;

        public Snowflake() {

            mySnowflakeIV = new ImageView(myContext);
            mySnowflakeIV.setImageDrawable(ContextCompat.getDrawable(myContext,R.drawable.menu_snow_1));
            mySnowflakeIV.setLayoutParams(new RelativeLayout.LayoutParams(50,50));
            mySnowflakeIV.setX(devWidth/2);
            mySnowflakeIV.setY(devHeight/2);
            mySnowflakeIV.setAlpha(0f);
            mainRelativeLayout.addView(mySnowflakeIV);



            animatorSet = createAnimation(true);
            startedTime = System.currentTimeMillis();
        }

        void startAnimation(){
            animatorSet.start();
        }

        private int counter = 0;
        private AnimatorSet createAnimation(boolean firstAnimationStart) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // When animation ends hide completely snowflake
                    if (counter%60 == 0)
                        ((Activity) myContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mySnowflakeIV.setAlpha(0f);
                            }
                        });

                    Log.e("ANIMATION COUNTER: " ,String.valueOf(++counter));
                    createAnimation(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            animatorSet.play(newYPosValueAnimator())
                    .with(rotationAnimation())
                    .with(alphaAnimation());
            animatorSet.setDuration(200);

            // first start is synchronized from main run thread, next are invoked recursively
            if (!firstAnimationStart)
                animatorSet.start();
            return animatorSet;
        }

        private Animator alphaAnimation() {
            ValueAnimator alphaAnimation;
            alphaAnimation = ValueAnimator.ofFloat(mySnowflakeIV.getAlpha()
                    , mySnowflakeIV.getAlpha()+0.03f);
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

        private ValueAnimator newYPosValueAnimator() {
            ValueAnimator yPosChange;
            //1
            yPosChange = ValueAnimator.ofFloat(mySnowflakeIV.getY()
                    , mySnowflakeIV.getY()-15);
            //2
            yPosChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //3
                    float value = (float) animation.getAnimatedValue();
                    //4
                    mySnowflakeIV.setTranslationY(value);
                }

            });

            yPosChange.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            yPosChange.setRepeatCount(0);
            yPosChange.setInterpolator(new LinearInterpolator());

            return yPosChange;
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
        // RUN function objects pause
        isThatOk = false;
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myThread = null;


    }

    private int animationList = 0;
    @Override
    public void run() {
        while (isThatOk) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ((Activity) myContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
