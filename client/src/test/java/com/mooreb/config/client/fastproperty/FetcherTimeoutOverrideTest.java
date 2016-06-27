package com.mooreb.config.client.fastproperty;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.mooreb.config.common.ConfigUtils;
import com.mooreb.config.common.environment.Locator;
import org.junit.BeforeClass;
import org.junit.Test;

public class FetcherTimeoutOverrideTest {

  private static final long OVERRIDE_BLOCK_TIME_MILLIS = 5000;
  private static final long OVERRIDE_BLOCK_COUNT = 10;

  @BeforeClass
  public static void setTimeout() {
    System.setProperty("com.mooreb.config.firstFetchBlockTimeMillis", Long.toString(
      OVERRIDE_BLOCK_TIME_MILLIS));
    System.setProperty("com.mooreb.config.firstFetchBlockCount", Long.toString(OVERRIDE_BLOCK_COUNT));
  }

  @Test
  public void proveThatTheFetcherIsBlocking() {

    final Fetcher fetcher = new Fetcher(new BlockingLocator());
    assertEquals("Fetcher should be configured with override block time",
      OVERRIDE_BLOCK_TIME_MILLIS,
      Fetcher.timeToBlockForFirstFetchValueMillis);
    assertEquals("Fetcher should be configured with override block count", OVERRIDE_BLOCK_COUNT,
      Fetcher.numTimesToBlockForFirstFetch);
    final FastProperty dummyProperty = new DummyProperty("test.property");
    long start = System.currentTimeMillis();
    fetcher.assign(dummyProperty);
    long duration = System.currentTimeMillis() - start;

    assertTrue(duration > 2500); // check that we have blocked for longer than the default
    assertTrue(fetcher.numSuccessfulFetchesSinceBoot.get() > 0); // Ensure we actually have returned a value

  }


  // Dummy implementation so we don't go off attempting to actually fetch
  private static final class DummyProperty extends FastProperty {

    protected DummyProperty(String propertyName) {
      super(propertyName);
    }

    @Override
    public void resetToDefault() {}

    @Override
    void assignFromString(String value) {}
  }

  // Dummy Locator implementation that just sleeps for 3 seconds in a method used by the Fetcher
  private static final class BlockingLocator implements Locator {

    public String getThisFQDN() {
      return null;
    }

    public String[] getConfigServiceFQDNs() {
      return new String[0];
    }

    public String getConfigServiceHostScheme() {
      return null;
    }

    public int getConfigServiceHostPort() {
      return 0;
    }

    public String getConfigServiceVip() {
      return null;
    }

    public int getConfigServiceVipPort() {
      return 0;
    }

    public String getConfigServiceVipScheme() {
      ConfigUtils.safeSleep(3000);
      return null;
    }

    public Map<String, String> getConfigClientContextAsKeyValuePairs() {
      return null;
    }

    public int getDBPort() {
      return 0;
    }

    public String getDBHost() {
      return null;
    }

    public String getDBName() {
      return null;
    }

    public String getJDBCConnectionString() {
      return null;
    }

    public String getJDBCUser() {
      return null;
    }

    public String getJDBCPassword() {
      return null;
    }

    public Connection getDBConnection() throws SQLException {
      return null;
    }

    public String getEnvironment() {
      return null;
    }
  }
}
