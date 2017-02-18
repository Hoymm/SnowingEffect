package com.hoymm.damianmuca.snowingeffect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    private int localSnowflakesAmount, localSnowflakesFallingSpeed;
    private final boolean useAccelerometrEnabled, isFirstSnowflakeActive
            , isSecondSnowflakeActive, isThirdSnowflakeActive;

    private SharedPreferences sharedPref;
    private ArrayList<Snowflake> mySnowflakesAL;

    // other objects
    private float devWidth, devHeight, density;
    // adjustDestinationAngle calculate random destination angle for current snowflake, then its is adding to the ground destination
    // and imitates little wind, i.e. ground has 140 degrees then, we add value of adjustDestinationAngle (i.e. -9 degree),
    // and we get designation angle eqaul to 131 degrees
    private double radius;
    private RelativeLayout mainRelativeLayout;
    private int myDegrees = 0;


    long startTime;
    private TextView timeTV, timeFromStartTV;

    // implements runnable objects
    private Thread myThread = null;
    private boolean isThatOk = true;
    public SnowGeneratorClass(
            Context context
            , final View view
            , final int snowflakesAmount
            , final int snowflakesFallingSpeed
            , boolean useAccelerometrEnabled
            , final boolean isFirstSnowflakeActive
            , final boolean isSecondSnowflakeActive
            , final boolean isThirdSnowflakeActive
    ) {

        // ##### ASSIGN OBJECTS
        myContext = context;
        this.view = view;

        startTime = System.currentTimeMillis();
        timeTV = (TextView) view.findViewById(R.id.time_tv_id);
        timeFromStartTV = (TextView) view.findViewById(R.id.time_from_start_tv_id);

        // get SharedPreferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(myContext);

        // assing arguments
        this.localSnowflakesAmount = snowflakesAmount/3 == 0 ? 3 : snowflakesAmount-(snowflakesAmount%3);
        this.localSnowflakesFallingSpeed = (StaticValues.getSnowflakesFallingTime()/(snowflakesFallingSpeed+10));
        this.useAccelerometrEnabled = useAccelerometrEnabled;
        this.isFirstSnowflakeActive = isFirstSnowflakeActive;
        this.isSecondSnowflakeActive = isSecondSnowflakeActive;
        this.isThirdSnowflakeActive = isThirdSnowflakeActive;

        // ##### INIT OBJECTS
        // WIDTH and HEIGHT
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        devWidth = displayMetrics.widthPixels;
        devHeight = displayMetrics.heightPixels;
        radius = (Math.sqrt(Math.pow(devWidth/2,2) + Math.pow(devHeight/2,2)));
        density = displayMetrics.density;

        // Initializate accelerometer
        if (useAccelerometrEnabled)
            initAccelerometer();
        else {
            myDegrees = 0;
            sensorChecked = true;
        }
        mainRelativeLayout = (RelativeLayout) view.findViewById(R.id.activity_set_wallpaper);

        // init snowflakes by id drawables
        mySnowflakesAL = new ArrayList<>();


        Thread snowflakesInitThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (snowflakesInitIndex < localSnowflakesAmount) {
                        // when accelerometer does not work yet, skip code
                        if (!sensorChecked) {
                            continue;
                        }
                        // run next animation
                        if (isFirstSnowflakeActive || isSecondSnowflakeActive || isThirdSnowflakeActive)
                            ((Activity) myContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    // if no snoflake type enabled, then skip it
                                    if (snowflakesInitIndex < localSnowflakesAmount) {
                                        if (isFirstSnowflakeActive) {
                                            mySnowflakesAL.add(new Snowflake(R.drawable.menu_snow_1));
                                            mySnowflakesAL.get(snowflakesInitIndex).startAnimation
                                                    (snowflakesInitIndex
                                                            * localSnowflakesFallingSpeed / localSnowflakesAmount);
                                            ++snowflakesInitIndex;
                                        }
                                        if (isSecondSnowflakeActive) {
                                            mySnowflakesAL.add(new Snowflake(R.drawable.menu_snow_2));
                                            mySnowflakesAL.get(snowflakesInitIndex).startAnimation
                                                    (snowflakesInitIndex
                                                            * localSnowflakesFallingSpeed / localSnowflakesAmount);
                                            ++snowflakesInitIndex;
                                        }
                                        if (isThirdSnowflakeActive) {
                                            mySnowflakesAL.add(new Snowflake(R.drawable.menu_snow_3));
                                            mySnowflakesAL.get(snowflakesInitIndex).startAnimation
                                                    (snowflakesInitIndex
                                                            * localSnowflakesFallingSpeed / localSnowflakesAmount);
                                            ++snowflakesInitIndex;
                                        }
                                    }
                                }
                            });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        snowflakesInitThread.start();



    }


    boolean sensorChecked = false;
    private void initAccelerometer() {
        // Check orientation for changing direction of smoke rising
        OrientationEventListener myOrientationEventListener =
                new OrientationEventListener(myContext, SensorManager.SENSOR_DELAY_GAME) {
            @Override
            public void onOrientationChanged(int currentDegrees) {
                // TODO Auto-generated method stub
                int naturalOrientation =
                        ((WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE))
                                .getDefaultDisplay().getRotation();

                // ADJUST FOR OTHER DEVICES, some devices have other accelerometer values then others,
                // we adjust it
                if (naturalOrientation == Surface.ROTATION_0) {
                    //Log.d("Rotation ROTATION: ", "0");
                } else if (naturalOrientation == Surface.ROTATION_90) {
                    //Log.d("Rotation ROTATION: ", "90");
                    currentDegrees += 90;
                } else if (naturalOrientation == Surface.ROTATION_180) {
                    //Log.d("Rotation ROTATION: ", "180");
                    currentDegrees += 180;
                } else if (naturalOrientation == Surface.ROTATION_270) {
                    //Log.d("Rotation ROTATION: ", "270");
                    currentDegrees += 270;
                }
                // Check if we have gone too far forward with rotation adjustment, keep the result
                // between 0-360
                if (currentDegrees > 360) {
                    currentDegrees -= 360;
                }

                myDegrees = currentDegrees; // goes from left to top (9 hour clockwise) 0 degree to 360
                TextView tempSensor = (TextView) view.findViewById(R.id.temp_sensor_tv_id);
                tempSensor.setText("DEV ANGLE: " + String.valueOf(myDegrees) + " degrees");
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

    static int elementIndex = 0;
    // Snowflake class create snowflake animation and controls it
    private class Snowflake {
        boolean generatedNewYPosition = false, generatedNewXPosition = false;

        // snowflake falling speed is expressed by distance of resolution to cover over cycle animation (i.e. 1000 miliseconds)
        private ImageView mySnowflakeIV;
        // snowflake angle falling to ground
        AnimatorSet animatorSet;
        int snowflakeWidth;
        int curItem = 0;
        // allows snow to fall in different lines (wind simulation) not only in single one line for all same, but in many different ways
        int randomAngleAdjust;

        public Snowflake(int drawableId) {
            mySnowflakeIV = new ImageView(myContext);
            mySnowflakeIV.setImageDrawable(ContextCompat.getDrawable(myContext,drawableId));
            mySnowflakeIV.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            curItem = elementIndex++;
            // drawable size
            snowflakeWidth = getRandomDrawableSize();
            mySnowflakeIV.setLayoutParams(new RelativeLayout.LayoutParams(snowflakeWidth,snowflakeWidth));


            randomAngleAdjust = (-StaticValues.getFallingAngleVariation()/2)
                    + (int) (Math.random()*StaticValues.getFallingAngleVariation());

            // add drawable to screen
            mainRelativeLayout.addView(mySnowflakeIV);

            // create animation
            mySnowflakeIV.setAlpha(0f);
            createAnimation();
        }
        void onPause(){

            mySnowflakeIV.setImageDrawable(null);
        }

        private void generateNewStartPosition() {
            generatedNewYPosition = true;
            generatedNewXPosition = true;
            // prevent myDegrees to be 0, (because 0 and 360 is the same number)
            myDegrees = myDegrees == 0 ? 360 : myDegrees;
            // generate random angle (myDegrees-90;myDegrees+90)
            int randomAngle = (int) ((myDegrees-80) + Math.random()*(160)+1);
            int randomRadius =  1 + (int)(Math.random()*(radius-1)+1);
            double randomDistanceToGenerateSnowHigher = radius*1/3 + Math.random()*(radius/2);


            float xPos = (float)(devWidth/2
                    - snowflakeWidth/2
                    - (Math.sin(Math.toRadians(randomAngle)) * randomRadius)
                    - ((Math.sin(Math.toRadians(myDegrees))) * randomDistanceToGenerateSnowHigher));
            if (curItem==0)
                Log.e("generate new X POS", (int)xPos+"");


            float yPos = (float)(devHeight/2
                    - snowflakeWidth/2
                    // set snowflake random position
                    - (Math.cos(Math.toRadians(randomAngle)) * randomRadius)
                    // adjust snowflake little higher than it's originally supposed
                    - ((Math.cos(Math.toRadians(myDegrees))) * randomDistanceToGenerateSnowHigher));
            //Log.e("Y POS", (int)yPos+"");


            mySnowflakeIV.setTranslationX((float)(devWidth/2
                    - snowflakeWidth/2
                    - (Math.sin(Math.toRadians(randomAngle)) * randomRadius)
                    - ((Math.sin(Math.toRadians(myDegrees))) * randomDistanceToGenerateSnowHigher)));

            mySnowflakeIV.setTranslationY((float)(devHeight/2
                    - snowflakeWidth/2
                    // set snowflake random position
                    - (Math.cos(Math.toRadians(randomAngle)) * randomRadius)
                    // adjust snowflake little higher than it's originally supposed
                    - ((Math.cos(Math.toRadians(myDegrees))) * randomDistanceToGenerateSnowHigher)));
            lastAnimatedValueY = yPos;
            lastAnimatedValueX = xPos;
        }

        void startAnimation(int startDelay){
            animatorSet.setStartDelay(startDelay);
            animatorSet.start();
        }

        private void createAnimation() {
            animatorSet = new AnimatorSet();

            int randomFallingLength = (int)(Math.random()*radius+radius*3/2);
            // first start is synchronized from main run thread, next are invoked recursively
            animatorSet.play(animHandleMethod(randomFallingLength));
        }

        // ROTATION
        private ValueAnimator rotationAnimation() {

            // ROTATION
            ValueAnimator rotateAnimation;
            int randomAnglesPerSecondRotationSpeed = StaticValues.getSnowflakesRotationMinSpeed()
                    + (int) (Math.random()
                    *(StaticValues.getSnowflakesRotationMaxSpeed()-StaticValues.getSnowflakesRotationMinSpeed()));


            rotateAnimation = ValueAnimator.ofFloat(0, (randomAnglesPerSecondRotationSpeed* localSnowflakesFallingSpeed)/100);
            rotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mySnowflakeIV.setRotation(value);
                }
            });
            rotateAnimation.setDuration(localSnowflakesFallingSpeed);
            rotateAnimation.setRepeatCount(0);
            return rotateAnimation;
        }

        // Y POINT DESTINATION
        float lastAnimatedValueY = 0;
        long tempRefreshTime = 0;
        private ValueAnimator animHandleMethod(int randomFallingLength) {
            ValueAnimator Y_Movement = ValueAnimator.ofFloat(0, randomFallingLength);
            lastAnimatedValueY = mySnowflakeIV.getTranslationY();

            final ValueAnimator snowflakeAlphaVA =  alphaAnimation();
            final ValueAnimator xSnowflakeMovementVA =  X_Movement(randomFallingLength);
            final ValueAnimator snowflakeRotationVA =  rotationAnimation();
            //##########################################################################################
            //              Y MOVEMENT           -           LISTENERS
            //##########################################################################################
            Y_Movement.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    snowflakeAlphaVA.start();
                    snowflakeRotationVA.start();
                    generateNewStartPosition();
                    xSnowflakeMovementVA.start();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    snowflakeAlphaVA.start();
                    snowflakeRotationVA.start();
                    generateNewStartPosition();
                    xSnowflakeMovementVA.start();
                }
            });
                    Y_Movement.setRepeatCount(ValueAnimator.INFINITE);
            Y_Movement.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tempRefreshTime = System.currentTimeMillis();
                    //3
                    float value = (float) animation.getAnimatedValue();
                    //4
                    /*if (curItem == 0)
                        Log.e(curItem
                                + " getTranslationY() ", mySnowflakeIV.getTranslationY()
                                + "\tLastAnimated_Y: " + lastAnimatedValueY
                                + "\tValue: " + lastAnimatedValueY
                                + "\tMath.abs(): " + Math.abs(value-lastAnimatedValueY)
                                + "\tvalue-lastAnimatedValueY: " + (value-lastAnimatedValueY)
                                + "\tgetAnimatedFraction(): " + animation.getAnimatedFraction()

                        );*/

                    float pixelsToMove;
                    // security IF, prevents snowflake jumping over screen when generated new pos
                    if (Math.abs(value-lastAnimatedValueY)>50.0f)
                        pixelsToMove = 0.9f;
                    else
                        pixelsToMove = Math.abs(value-lastAnimatedValueY);
                    mySnowflakeIV.setTranslationY(mySnowflakeIV.getTranslationY()
                            + (float)(pixelsToMove*Math.cos(Math.toRadians(myDegrees+randomAngleAdjust))));
                    if (curItem==0)
                        Log.e("Pixels To Move", pixelsToMove
                                + "\tvalue == " + value
                                + "\tlastAnimatedValueY == " + lastAnimatedValueY
                        );
                    lastAnimatedValueY = value;
                }

            });
            Y_Movement.setDuration(localSnowflakesFallingSpeed);
            Y_Movement.setInterpolator(new LinearInterpolator());
            return Y_Movement;
        }

        // alpha animation (showing at start and hiding at the end each snowflake)
        private ValueAnimator alphaAnimation() {
            final ValueAnimator alphaShowAnimation = ValueAnimator.ofFloat(0.0f, 1.0f), alphaHideAnimation;

            // ########## UPDATE LISTENER AlphaHide
            alphaHideAnimation = ValueAnimator.ofFloat(1.0f, .0f);
            alphaHideAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mySnowflakeIV.setAlpha(value);
                }
            });
            alphaHideAnimation.setDuration(StaticValues.getSnowflakesAlphaDuration());
            alphaHideAnimation.setRepeatCount(0);

            // ########## UPDATE LISTENER AlphaShow
            alphaShowAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mySnowflakeIV.setAlpha(value);
                }
            });

            // ########## ADAPTER
            alphaShowAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    alphaHideAnimation.setStartDelay(localSnowflakesFallingSpeed - (StaticValues.getSnowflakesAlphaDuration() * 2 + 1000));
                    alphaHideAnimation.start();
                }
            });
                    alphaShowAnimation.setDuration(StaticValues.getSnowflakesAlphaDuration());
            alphaShowAnimation.setRepeatCount(0);
            return alphaShowAnimation;
        }

        float lastAnimatedValueX;
        // X POINT DESTINATION
        private ValueAnimator X_Movement(int randomFallingLength) {

            ValueAnimator X_Movement;
            X_Movement = ValueAnimator.ofFloat(mySnowflakeIV.getTranslationX()
                        , (int)(mySnowflakeIV.getTranslationX()+radius+randomFallingLength));
            lastAnimatedValueX = mySnowflakeIV.getTranslationX();
            //2
            X_Movement.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //3
                    float value = (float) animation.getAnimatedValue();


                    float pixelsToMove;
                    // security IF, prevents snowflake jumping over screen when generated new pos
                    if (Math.abs(value-lastAnimatedValueX)>50.0f)
                        pixelsToMove = 0.9f;
                    else
                        pixelsToMove = Math.abs(value-lastAnimatedValueX);
                    mySnowflakeIV.setTranslationX(mySnowflakeIV.getTranslationX()
                            + (float)(pixelsToMove*Math.sin(Math.toRadians(myDegrees+randomAngleAdjust))));
                    lastAnimatedValueX = value;
                }

            });

            X_Movement.setDuration(localSnowflakesFallingSpeed);
            X_Movement.setInterpolator(new LinearInterpolator());
            X_Movement.setRepeatCount(0);
            return X_Movement;
        }

        void destroyAnimation(){
            mySnowflakeIV.setLayerType(View.LAYER_TYPE_NONE, null);
        }
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
        myThread = new Thread(this);
        myThread.start();
    }

    void onPause(){
        // RUN function objects pause
        isThatOk = false;
        try {
            if (myThread!=null)
                myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myThread = null;
    }

    void onDestroy(){
        for (int i = 0 ; i < mySnowflakesAL.size(); ++i){
            mySnowflakesAL.get(i).onPause();
            mySnowflakesAL.get(i).destroyAnimation();
            mySnowflakesAL.remove(i);
        }
    }

    private int snowflakesInitIndex = 0;
    @Override
    public void run() {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
