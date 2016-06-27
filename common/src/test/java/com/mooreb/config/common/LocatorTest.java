package com.mooreb.config.common;

import com.mooreb.config.common.environment.DefaultLocator;
import com.mooreb.config.common.environment.Localhost8080Locator;
import com.mooreb.config.common.environment.Locator;
import org.junit.Assert;
import org.junit.Test;

public class LocatorTest {
    @Test
    public void getJDBCConnectionStringTest() {
        Locator locator = new Localhost8080Locator();
        String jdbcConnectionString = locator.getJDBCConnectionString();
        Assert.assertNotNull(jdbcConnectionString);
        Assert.assertEquals("did the jdbc connector string change?",
                "jdbc:hsqldb:hsql://localhost:3333/com.mooreb.config",
                jdbcConnectionString);
    }

}
