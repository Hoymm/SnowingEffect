package com.hoymm.damianmuca.snowingeffect;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by root on 06.01.17.
 */

public class StaticValues {
    private static List <Drawable> firstSnowflakeType_L = null;
    private static List <Drawable> secondSnowflakeType_L = null;
    private static List <Drawable> thirdSnowflakeType_L = null;
    // MODIFY BELOW IF U WANT CHANGE DEFAULT VALUE OF SPEED
    // MODIFY BELOW IF U WANT CHANGE SPEEDING, BUT LEFT DEFAULT AS IT IS

    // ANIMATION REACTION DELAY (When changing device roation)
    private static final int ANIM_REACTION_DELAY = 1000;
    // FALLING TIME
    private static final float SNOWFLAKES_FALLING_TIME = 10000;  // Interval >1
    // FALLING SPEED
    private static final float SNOWFLAKES_SPEED_MULTIPLER_MIN = 0.001f;  // Interval >1
    private static final float SNOWFLAKES_SPEED_MULTIPLER_MAX = 0.002f;  // Interval >1
    // APLHA
    private static final float SNOWFLAKES_ALPHA_DURATION = 0.25f;  // Interval (0f;1f)
    // ROTATION
    private static final int SNOWFLAKES_ROTATION_MIN_SPEED = 1;  // Interval >1
    private static final int SNOWFLAKES_ROTATION_MAX_SPEED = 16;  // Interval >1
    // SIZE
    private static final int SNOWFLAKES_MAX_SIZE = 9;  // Interval 1 - 100
    private static final int SNOWFLAKES_MIN_SIZE = 5;  // Interval 1 - 100
    // WIND
    private static final int WIND_DEGREES_MIN = 86;
    private static final int WIND_DEGREES_MAX = 94;
    private static final float WIND_INTENSITY_MIN = 0.00001f;
    private static final float WIND_INTENSITY_MAX = 0.00006f;
    // DEFAULT SETTINGS
    private static final int SNOWFLAKES_AMOUNT_BY_DEFAULT = 25; // Interval 1 - 100
    private static final int SNOWFLAKES_SPEED_BY_DEFAULT = 5;  // Interval 1 - 100
    private static final boolean USE_ACCELEROMETR_BY_DEFAULT = true;
    private static final boolean USE_FIRST_SNOWFLAKE_BY_DEFAULT = true;
    private static final boolean USE_SECOND_SNOWFLAKE_BY_DEFAULT = true;
    private static final boolean USE_THIRD_SNOWFLAKE_BY_DEFAULT = true;
    private static final int HOW_MANY_TYPES_OF_SNOWFLAKES = 3;

    public static int getAnimReactionDelay() {
        return ANIM_REACTION_DELAY;
    }

    public static float getSnowflakesAlphaDuration() {
        return SNOWFLAKES_ALPHA_DURATION;
    }

    public static List<Drawable> getThirdSnowflakeType_L() {
        return thirdSnowflakeType_L;
    }

    public static void setThirdSnowflakeType_L(List<Drawable> thirdSnowflakeType_L) {
        StaticValues.thirdSnowflakeType_L = thirdSnowflakeType_L;
    }

    public static void addItemToThirdSnowflakeType(Drawable drawable){
        StaticValues.thirdSnowflakeType_L.add(drawable);
    }

    public static List<Drawable> getSecondSnowflakeType_L() {
        return secondSnowflakeType_L;
    }

    public static void setSecondSnowflakeType_L(List<Drawable> secondSnowflakeType_L) {
        StaticValues.secondSnowflakeType_L = secondSnowflakeType_L;
    }

    public static void addItemToSecondSnowflakeType(Drawable drawable){
        StaticValues.secondSnowflakeType_L.add(drawable);
    }

    public static List<Drawable> getFirstSnowflakeType_L() {
        return firstSnowflakeType_L;
    }

    public static void setFirstSnowflakeType_L(List<Drawable> firstSnowflakeType_L) {
        StaticValues.firstSnowflakeType_L = firstSnowflakeType_L;
    }

    public static void addItemToFirstSnowflakeType(Drawable drawable){
        StaticValues.thirdSnowflakeType_L.add(drawable);
    }




    public static float getSnowflakesFallingTime() {
        return SNOWFLAKES_FALLING_TIME;
    }

    public static float getWindIntensityMin() {
        return WIND_INTENSITY_MIN;
    }

    public static float getWindIntensityMax() {
        return WIND_INTENSITY_MAX;
    }

    public static int getWindDegreesMin() {
        return WIND_DEGREES_MIN;
    }

    public static int getWindDegreesMax() {
        return WIND_DEGREES_MAX;
    }

    public static int getSnowflakesRotationMinSpeed() {
        return SNOWFLAKES_ROTATION_MIN_SPEED;
    }

    public static int getSnowflakesRotationMaxSpeed() {
        return SNOWFLAKES_ROTATION_MAX_SPEED;
    }

    public static float getSnowflakesSpeedMultiplerMin() {
        return SNOWFLAKES_SPEED_MULTIPLER_MIN;
    }

    public static int getHowManyTypesOfSnowflakes() {
        return HOW_MANY_TYPES_OF_SNOWFLAKES;
    }

    public static float getSnowflakesSpeedMultiplerMax() {
        return SNOWFLAKES_SPEED_MULTIPLER_MAX;
    }

    public static int getSnowflakesMinSize() {
        return SNOWFLAKES_MIN_SIZE;
    }

    public static int getSnowflakesMaxSize() {
        return SNOWFLAKES_MAX_SIZE;
    }

    public static int getSnowflakesSpeedByDefault() {
        return SNOWFLAKES_SPEED_BY_DEFAULT;
    }

    public static int getSnowflakesAmountByDefault() {
        return SNOWFLAKES_AMOUNT_BY_DEFAULT;
    }

    public static boolean isUseAccelerometrByDefault() {
        return USE_ACCELEROMETR_BY_DEFAULT;
    }

    public static boolean isUseFirstSnowflakeByDefault() {
        return USE_FIRST_SNOWFLAKE_BY_DEFAULT;
    }

    public static boolean isUseSecondSnowflakeByDefault() {
        return USE_SECOND_SNOWFLAKE_BY_DEFAULT;
    }

    public static boolean isUseThirdSnowflakeByDefault() {
        return USE_THIRD_SNOWFLAKE_BY_DEFAULT;
    }
}
