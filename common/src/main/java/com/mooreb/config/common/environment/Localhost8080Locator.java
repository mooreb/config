package com.mooreb.config.common.environment;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class Localhost8080Locator implements Locator {
    public String getThisFQDN() {
        return "localhost";
    }

    public String[] getConfigServiceFQDNs() {
        String[] retval = new String[1];
        retval[0] = "localhost";
        return retval;
    }

    public int getConfigServiceHostPort() {
        return 8080;
    }

    public String getConfigServiceVip() {
        return "localhost";
    }

    public int getConfigServiceVipPort() {
        return 8080;
    }

    public Map<String, String> getConfigClientContextAsKeyValuePairs() {
        return null;
    }

    public String getConfigServiceHostScheme() {
        return "http";
    }

    public String getConfigServiceVipScheme() {
        return "http";
    }

    public int getDBPort() {
        return 3333;
    }

    public String getDBHost() {
        return "localhost";
    }

    public String getDBName() {
        return "com.mooreb.config";
    }

    public String getJDBCConnectionString() {
        return String.format("jdbc:hsqldb:hsql://%s:%d/%s", getDBHost(), getDBPort(), getDBName());
    }

    public String getJDBCUser() {
        return "SA";
    }

    public String getJDBCPassword() {
        return "";
    }

    public Connection getDBConnection() throws SQLException {
        Connection retval = DriverManager.getConnection(
                getJDBCConnectionString(),
                getJDBCUser(),
                getJDBCPassword());
        retval.setAutoCommit(false);
        return retval;
    }

  public String getEnvironment() {
    return "local";
  }
}
