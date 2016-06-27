package com.mooreb.config.service.backup;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.mooreb.config.common.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    protected static File[] getDumpFiles(String kindOfDump) {
        File mid = ConfigUtils.getMidLevelDirectoryForDumps(kindOfDump);
        if(null == mid) return new File[0];
        return mid.listFiles();
    }

    protected static void sortByFilename(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String n1 = o1.getName();
                String n2 = o2.getName();
                return (n1.compareTo(n2));
            }
        });
    }

    protected static File getOutputFileForDump(long now, String kindOfDump) {
        final File mid = ConfigUtils.getMidLevelDirectoryForDumps(kindOfDump);
        if (mid == null) return null;
        return new File(mid, Long.toString(now));
    }

    protected static void cleanupOldDumps(String kindOfDump, int numToKeep) {
        File[] files = getDumpFiles(kindOfDump);
        int numFiles = files.length;
        if(numFiles > numToKeep) {
            sortByFilename(files);
            final int numToDelete = numFiles - numToKeep;
            for(int i=0; i<numToDelete; i++) {
                final File f = files[i];
                boolean success = f.delete();
                if(!success) {
                    LOGGER.error("cannot delete " + f);
                }
            }
        }
    }
}
