package com.mooreb.config.common.environment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class DummyLocator implements Locator {
    public String getThisFQDN() {
        return null;
    }

    public String[] getConfigServiceFQDNs() {
        return new String[0];
    }

    public String getConfigServiceVip() {
        return null;
    }

    public int getConfigServiceHostPort() {
        return 0;
    }

    public int getConfigServiceVipPort() {
        return 0;
    }

    public Map<String, String> getConfigClientContextAsKeyValuePairs() {
        return Collections.emptyMap();
    }

    public String getConfigServiceHostScheme() {
        return null;
    }

    public String getConfigServiceVipScheme() {
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
