package com.hoymm.damianmuca.snowingeffect;

/**
 * Created by root on 06.01.17.
 */

public class StaticValues {
    private static final int SNOWFLAKES_AMOUNT_BY_DEFAULT = 20; // Interval 1 - 100


    // MODIFY BELOW IF U WANT CHANGE DEFAULT VALUE OF SPEED
    private static final int SNOWFLAKES_SPEED_BY_DEFAULT = 20;  // Interval 1 - 100
    // MODIFY BELOW IF U WANT CHANGE SPEEDING, BUT LEFT DEFAULT AS IT IS
    private static final int SNOWFLAKES_SPEED_MULTIPLER = 250;  // Interval >1
    private static final int SNOWFLAKES_ALPHA_DURATION = 2500;


    private static final int SNOWFLAKES_MAX_SIZE = 13;  // Interval 1 - 100
    private static final int SNOWFLAKES_MIN_SIZE = 5;  // Interval 1 - 100

    private static final boolean USE_ACCELEROMETR_BY_DEFAULT = true;
    private static final boolean USE_FIRST_SNOWFLAKE_BY_DEFAULT = true;
    private static final boolean USE_SECOND_SNOWFLAKE_BY_DEFAULT = true;
    private static final boolean USE_THIRD_SNOWFLAKE_BY_DEFAULT = true;

    public static int getSnowflakesAlphaDuration() {
        return SNOWFLAKES_ALPHA_DURATION;
    }

    public static int getSnowflakesSpeedMultipler() {
        return SNOWFLAKES_SPEED_MULTIPLER;
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
