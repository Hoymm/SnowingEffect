package com.hoymm.damianmuca.snowingeffect;

/**
 * Created by root on 06.01.17.
 */

public class StaticValues {
    // MODIFY BELOW IF U WANT CHANGE DEFAULT VALUE OF SPEED
    // MODIFY BELOW IF U WANT CHANGE SPEEDING, BUT LEFT DEFAULT AS IT IS

    // FALLING TIME
    private static final float SNOWFLAKES_FALLING_TIME = 10000;  // Interval >1
    // FALLING SPEED
    private static final float SNOWFLAKES_SPEED_MULTIPLER_MIN = 0.001f;  // Interval >1
    private static final float SNOWFLAKES_SPEED_MULTIPLER_MAX = 0.002f;  // Interval >1
    // APLHA
    private static final int SNOWFLAKES_ALPHA_DURATION = 2500;
    // ROTATION
    private static final int SNOWFLAKES_ROTATION_MIN_SPEED = 1;  // Interval >1
    private static final int SNOWFLAKES_ROTATION_MAX_SPEED = 16;  // Interval >1
    // SIZE
    private static final int SNOWFLAKES_MAX_SIZE = 8;  // Interval 1 - 100
    private static final int SNOWFLAKES_MIN_SIZE = 3;  // Interval 1 - 100
    // WIND
    private static final int WIND_DEGREES_MIN = 86;
    private static final int WIND_DEGREES_MAX = 94;
    private static final float WIND_INTENSITY_MIN = 0.00001f;
    private static final float WIND_INTENSITY_MAX = 0.00006f;
    // DEFAULT SETTINGS
    private static final int SNOWFLAKES_AMOUNT_BY_DEFAULT = 5; // Interval 1 - 100
    private static final int SNOWFLAKES_SPEED_BY_DEFAULT = 5;  // Interval 1 - 100
    private static final boolean USE_ACCELEROMETR_BY_DEFAULT = true;
    private static final boolean USE_FIRST_SNOWFLAKE_BY_DEFAULT = true;
    private static final boolean USE_SECOND_SNOWFLAKE_BY_DEFAULT = true;
    private static final boolean USE_THIRD_SNOWFLAKE_BY_DEFAULT = true;
    private static final int HOW_MANY_TYPES_OF_SNOWFLAKES = 3;

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

    public static int getSnowflakesAlphaDuration() {
        return SNOWFLAKES_ALPHA_DURATION;
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
