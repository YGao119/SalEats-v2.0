package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatter {

    private static final long startTime = System.currentTimeMillis();

    public static String getTimestamp() {
        return new SimpleDateFormat("HH:mm:ss.SS").format(new Date()).substring(0, 11);
    }

    public static String getTimestampDiff() {
        long duration = System.currentTimeMillis() - startTime;
        return new SimpleDateFormat("HH:mm:ss.SS").format(duration).substring(0, 11);
    }

}
