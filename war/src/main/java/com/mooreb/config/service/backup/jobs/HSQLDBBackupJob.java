package com.mooreb.config.service.backup.jobs;

import com.mooreb.config.common.environment.Locator;
import com.mooreb.config.service.backup.BackupJob;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSQLDBBackupJob extends BackupJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(HSQLDBBackupJob.class);
    private static final int numBackupDumpsToKeep = 300;
    private static ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(1);
    private static final long backupInitialDelay = 1;
    private static final long backupIntervalValue = 180;
    private static final TimeUnit backupIntervalUnits = TimeUnit.MINUTES;
    private static final String kindOfDump = "hsqldb-backup";

    public static void start(final Locator locator) {
        ScheduledFuture ignoredScheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                fireOnce(numBackupDumpsToKeep, locator);
                            }
                        },
                        backupInitialDelay,
                        backupIntervalValue,
                        backupIntervalUnits);
    }

    private HSQLDBBackupJob(){}

    private static void fireOnce(int numToKeep, Locator locator) {
        final long now = System.currentTimeMillis();
        writeBackup(now, locator);
        cleanupOldDumps(kindOfDump, numToKeep);
    }

    private static void writeBackup(long now, final Locator locator) {
        final File f = getOutputFileForDump(now, kindOfDump);
        if(null != f) {
            writeBackupFile(f, locator);
        }
    }

    private static void writeBackupFile(File f, Locator locator) {
        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken.");
                return;
            }
            final String filename = f.toString() + ".tar.gz";
            connection.createStatement().executeUpdate("BACKUP DATABASE TO '" + filename + "' BLOCKING");
        }
        catch(SQLException e) {
            LOGGER.error("cannot write a backup file", e);
        }
        finally {
            try {
                if(null != connection) { connection.close(); }
            }
            catch(SQLException e) {
                LOGGER.error("caught exception trying to close connection", e);
            }
        }
    }
}
