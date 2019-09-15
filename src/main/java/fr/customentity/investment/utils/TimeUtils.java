package fr.customentity.investment.utils;

/**
 * Created by CustomEntity on 07/03/2019 for [SpigotMc] Investment.
 */
public class TimeUtils {

    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    public static int hoursFromSeconds(int seconds) {
        return seconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
    }

    public static int minutesFromSeconds(int seconds) {
        return (seconds - (hoursToSeconds(hoursFromSeconds(seconds))))
                / SECONDS_IN_A_MINUTE;
    }

    public static int secondsFromSeconds(int seconds) {
        return seconds - ((hoursToSeconds(hoursFromSeconds(seconds))) + (minutesToSeconds(minutesFromSeconds(seconds))));
    }

    private static int hoursToSeconds(int hours) {
        return hours * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE;
    }

    private static int minutesToSeconds(int minutes) {
        return minutes * SECONDS_IN_A_MINUTE;
    }
}
