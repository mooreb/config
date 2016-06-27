package com.mooreb.config.client.fastproperty;

import com.mooreb.config.common.ConfigUtils;
import com.mooreb.config.common.environment.DummyLocator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FetcherTest {
    @Test @Ignore("This is currently disabled because it takes too long to run in an automated fashion. Additionally it doesn't need to be verified every time. Re-enable to test functionality.")
    public void proveThatTheFetcherIsRunning() {
        final Fetcher fetcher = new Fetcher(new DummyLocator());
        long numFetches1 = fetcher.numAttemptedFetchesSinceBoot.get();
        Assert.assertTrue("numFetches should be greater than or equal to zero", numFetches1 >= 0);
        long sleepMillis = TimeUnit.MILLISECONDS.convert(Fetcher.fetcherIntervalValue, Fetcher.fetcherIntervalUnits);
        long oneAndAHalfCycles = (long)(1.5 * sleepMillis);
        ConfigUtils.safeSleep(oneAndAHalfCycles);
        long numFetches2 = fetcher.numAttemptedFetchesSinceBoot.get();
        Assert.assertTrue("after sleeping 50% more than a cycle we should have seen at least one more fetch", numFetches2 > numFetches1);
    }

    @Test @Ignore("This is currently disabled because it takes too long to run in an automated fashion. Additionally it doesn't need to be verified every time. Re-enable to test functionality.")
    public void proveThatTheFetcherIsFetching() {
        final Fetcher fetcher = new Fetcher(new DummyLocator());
        // We need at least one property for the fetcher to fetch
        String uuid = UUID.randomUUID().toString();
        BooleanProperty bp = BooleanProperty.findOrCreate(uuid, true);
        long numFetches1 = fetcher.numSuccessfulFetchesSinceBoot.get();
        Assert.assertTrue("numFetches should be greater than or equal to zero", numFetches1 >= 0);
        long sleepMillis = TimeUnit.MILLISECONDS.convert(Fetcher.fetcherIntervalValue, Fetcher.fetcherIntervalUnits);
        long oneAndAHalfCycles = (long)(1.5 * sleepMillis);
        ConfigUtils.safeSleep(oneAndAHalfCycles);
        long numFetches2 = fetcher.numSuccessfulFetchesSinceBoot.get();
        Assert.assertTrue("after sleeping 50% more than a cycle we should have seen at least one more successful fetch", numFetches2 > numFetches1);
    }
}
