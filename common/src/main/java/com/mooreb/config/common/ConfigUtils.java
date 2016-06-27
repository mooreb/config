package com.mooreb.config.common;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

    public static void safeSleep(long timeToSleepInMillis) {
        long remainingSleepMilliseconds = timeToSleepInMillis;
        long sleepStarted;
        do {
            sleepStarted = System.currentTimeMillis();
            try {
                Thread.sleep(remainingSleepMilliseconds);
            } catch (InterruptedException e) {}
            long sleepEnded = System.currentTimeMillis();
            long elapsed = sleepEnded - sleepStarted;
            remainingSleepMilliseconds = remainingSleepMilliseconds - elapsed;
        } while(remainingSleepMilliseconds > 0);
    }

    public static File getMidLevelDirectoryForDumps(final String kindOfDump) {
        final File slashVar = new File(File.separator, "var");
        File root;
        if(slashVar.isDirectory()) {
            root = slashVar;
        }
        else {
            root = new File(System.getProperty("user.home"));
        }
        final File mid_low = new File(root, ".com.mooreb.config");
        if(!mid_low.exists()) {
            boolean success1 = mid_low.mkdir();
            if (!success1) {
                LOGGER.error("cannot mkdir " + mid_low);
                return null;
            }
        }
        final File mid_high = new File(mid_low, kindOfDump);
        if(!mid_high.exists()) {
            boolean success2 = mid_high.mkdir();
            if (!success2) {
                LOGGER.error("cannot mkdir " + mid_high);
                return null;
            }
        }
        return mid_high;
    }

    public static long safeGetPositiveLongSystemProperty(String key, long defaultValue) {
        long retval = defaultValue;
        try {
            retval = Long.valueOf(System.getProperty(key, Long.toString(defaultValue)));
            // Ensure we have a sensible value
            if (retval < 1) {
                LOGGER.warn("Invalid value of {}, defaulting to {}", key, defaultValue);
                return defaultValue;
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("Unable to parse value of {}, defaulting to {}", key, defaultValue, e);
        }
        return retval;
    }
}
