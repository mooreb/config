package com.mooreb.config.common.environment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface Locator {
    public String getThisFQDN();
    public String[] getConfigServiceFQDNs();
    public String getConfigServiceHostScheme();
    public int getConfigServiceHostPort();
    public String getConfigServiceVip();
    public int getConfigServiceVipPort();
    public String getConfigServiceVipScheme();
    public Map<String, String>getConfigClientContextAsKeyValuePairs();
    public int getDBPort();
    public String getDBHost();
    public String getDBName();
    public String getJDBCConnectionString();
    public String getJDBCUser();
    public String getJDBCPassword();
    public Connection getDBConnection() throws SQLException;
    public String getEnvironment();
}
