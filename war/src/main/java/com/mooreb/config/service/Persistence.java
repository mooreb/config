package com.mooreb.config.service;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mooreb.config.common.AuditLogEntry;
import com.mooreb.config.common.ConfigUtils;
import com.mooreb.config.common.Property;
import com.mooreb.config.common.environment.Locator;
import com.mooreb.config.service.backup.jobs.HSQLDBBackupJob;

public class Persistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(Persistence.class);
    private final Locator locator;

    public Persistence(final Locator locator) {
        this.locator = locator;
        init();
    }

    private void init() {
        if (!startHSQLDBServer()) return;
        if (!loadHSQLJDBCDriver()) return;
        createDBObjects();
    }

    /**
     *
     * @return false on failure, true otherwise
     */
    private boolean startHSQLDBServer() {
        final String dbHost = locator.getDBHost();
        if(null == dbHost) {
            LOGGER.warn("dbHost is null; persistence will be broken");
            return false;
        }
        final String me = locator.getThisFQDN();
        if(dbHost.equals(me)) {
            LOGGER.info("I AM the database server {}", dbHost);
            final Server server = new Server();
            final int dbPort = locator.getDBPort();
            server.setAddress(dbHost);
            server.setPort(dbPort);
            final File path = ConfigUtils.getMidLevelDirectoryForDumps("hsqldb");
            if(null == path) {
                LOGGER.error("null path for db files; persistence will be broken");
                return false;
            }
            final String dbName = locator.getDBName();
            if(null == dbName) {
                LOGGER.error("null name for db; persistence will be broken");
                return false;
            }
            server.setDatabasePath(0, "file:" + path.toString() + "/" + dbName);
            server.setDatabaseName(0, dbName);
            server.setTrace(true);
            server.setLogWriter(new PrintWriter(System.out));
            server.setErrWriter(new PrintWriter(System.err));
            server.start();
            HSQLDBBackupJob.start(locator);
            registerShutdownHook();
        }
        else {
            final String jdbcConnectionString = locator.getJDBCConnectionString();
            if(null == jdbcConnectionString) {
                LOGGER.error("jdbc connection string is null; will not be able to connect to any db. persistence will be broken.");
                return false;
            }
            LOGGER.info("I AM NOT the database server {}. I am {}. I will try to connect to {}",
                    dbHost,
                    me,
                    jdbcConnectionString);
        }
        return true;
    }

    /**
     *
     * @return false on failure, true otherwise
     */
    private boolean loadHSQLJDBCDriver() {
        try {
            final Class ignored = Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("we cannot find the hsqldb jdbc driver; persistence will be broken", e);
            return false;
        }
        return true;
    }

    private void createDBObjects() {
        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken. aborting init early.");
                return;
            }
            createPropertiesTable(connection);
            createPropertiesPrimaryKey(connection);
            createPropertiesIndices(connection);
            createAuditLogTable(connection);
            createAuditLogIndices(connection);
        }
        catch (SQLException e) {
            LOGGER.error("could not create db objects", e);
        }
        finally {
            mySafeClose(connection);
        }
    }

    private void createPropertiesTable(Connection connection) {
        final String sql = "CREATE TABLE properties (" +
                "uuid varchar(36)," +
                "propertyName varchar(1024)," +
                "context varchar(1024)," +
                "value varchar(1024)," +
                "author varchar(1024)," +
                "comments varchar(1024)," +
                "lastModified BIGINT" +
                ")";
        try {
            connection.createStatement().executeUpdate(sql);
        }
        catch(SQLException e) {
            LOGGER.error("Could not create properties table", e);
        }
    }

    private void createPropertiesPrimaryKey(Connection connection) {
        final String sql = "ALTER TABLE properties ADD PRIMARY KEY(uuid)";
        try {
            connection.createStatement().executeUpdate(sql);
        }
        catch(SQLException e) {
            LOGGER.error("Could not create properties primary key", e);
        }
    }

    private void createPropertiesIndices(Connection connection) {
        createPropertyNameIndex(connection);
        createLastModifiedIndex(connection);
    }

    private void createPropertyNameIndex(Connection connection) {
        final String sql = "CREATE INDEX properties_propertyName_idx ON properties(propertyName)";
        try {
            connection.createStatement().executeUpdate(sql);
        }
        catch(SQLException e) {
            LOGGER.error("Could not create property name index", e);
        }
    }

    private void createLastModifiedIndex(Connection connection) {
        final String sql = "CREATE INDEX properties_lastModified_idx ON properties(lastModified)";
        try {
            connection.createStatement().executeUpdate(sql);
        }
        catch(SQLException e) {
            LOGGER.error("Could not create lastModified index", e);
        }
    }

    private void createAuditLogTable(Connection connection) {
        final String sql = "CREATE TABLE auditlog (" +
                "timestamp BIGINT," +
                "action varchar(1024)," +
                "author varchar(1024)," +
                "comment varchar(1024)," +
                "olduuid varchar(1024)," +
                "oldname varchar(1024)," +
                "oldcontext varchar(1024)," +
                "oldvalue varchar(1024)," +
                "oldauthor varchar(1024)," +
                "oldcomments varchar(1024)," +
                "oldLastModified BIGINT," +
                "newuuid varchar(1024)," +
                "newname varchar(1024)," +
                "newcontext varchar(1024)," +
                "newvalue varchar(1024)," +
                "newauthor varchar(1024)," +
                "newcomments varchar(1024)," +
                "newLastModified BIGINT" +
                ")";
        try {
            connection.createStatement().executeUpdate(sql);
        }
        catch(SQLException e) {
            LOGGER.error("Could not create auditlog table", e);
        }
    }

    private void createAuditLogIndices(Connection connection) {
        createTimestampIndex(connection);
    }

    private void createTimestampIndex(Connection connection) {
        final String sql = "CREATE INDEX auditlog_timestamp_idx ON auditlog(timestamp)";
        try {
            connection.createStatement().executeUpdate(sql);
        }
        catch(SQLException e) {
            LOGGER.error("Could not create property name index", e);
        }
    }

    public List<Property> getAllProperties() {
        List<Property> retval = new ArrayList<Property>();
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken. returning an empty list of propertyes");
                return Collections.emptyList();
            }

            resultSet = connection.createStatement()
                    .executeQuery(
                            "SELECT uuid, propertyName, context, value, author, comments, lastModified " +
                                    "FROM properties ORDER BY propertyName ASC, lastModified DESC");
            while(resultSet.next()) {
                final String uuid = resultSet.getString("uuid");
                final String propertyName = resultSet.getString("propertyName");
                final String context = resultSet.getString("context");
                final String value = resultSet.getString("value");
                final String author = resultSet.getString("author");
                final String comments = resultSet.getString("comments");
                final long lastModified = resultSet.getLong("lastModified");
                final Property p = new Property(uuid, propertyName, context, value, author, comments, lastModified);
                retval.add(p);
            }
        }
        catch(SQLException e) {
            LOGGER.error("cannot get list. jdbcString={}", locator.getJDBCConnectionString(), e);
        }
        finally {
            mySafeClose(resultSet);
            mySafeClose(connection);
        }
        return Collections.unmodifiableList(retval);
    }

    public List<AuditLogEntry> getAuditLog() {
        List<AuditLogEntry> retval = new ArrayList<AuditLogEntry>();
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken. returning an empty list of audit log entries.");
                return Collections.emptyList();
            }

            resultSet = connection.createStatement()
                    .executeQuery(
                            "SELECT timestamp, action, author, comment, " +
                                    "olduuid, oldname, oldcontext, oldvalue, oldauthor, oldcomments, oldLastModified, " +
                                    "newuuid, newname, newcontext, newvalue, newauthor, newcomments, newLastModified " +
                                    "FROM auditlog ORDER BY timestamp DESC");
            while(resultSet.next()) {
                final long timestamp = resultSet.getLong("timestamp");
                final String action = resultSet.getString("action");
                final String author = resultSet.getString("author");
                final String comment = resultSet.getString("comment");
                final String olduuid = resultSet.getString("olduuid");
                final String oldname = resultSet.getString("oldname");
                final String oldcontext = resultSet.getString("oldcontext");
                final String oldvalue = resultSet.getString("oldvalue");
                final String oldauthor = resultSet.getString("oldauthor");
                final String oldcomments = resultSet.getString("oldcomments");
                final long oldLastModified = resultSet.getLong("oldLastModified");
                final Property oldProperty = new Property(olduuid, oldname, oldcontext, oldvalue, oldauthor, oldcomments, oldLastModified);
                final String newuuid = resultSet.getString("newuuid");
                final String newname = resultSet.getString("newname");
                final String newcontext = resultSet.getString("newcontext");
                final String newvalue = resultSet.getString("newvalue");
                final String newauthor = resultSet.getString("newauthor");
                final String newcomments = resultSet.getString("newcomments");
                final long newLastModified = resultSet.getLong("newLastModified");
                final Property newProperty = new Property(newuuid, newname, newcontext, newvalue, newauthor, newcomments, newLastModified);
                final AuditLogEntry ale = new AuditLogEntry(action, author, oldProperty, newProperty, comment, timestamp);
                retval.add(ale);
            }
        }
        catch(SQLException e) {
            LOGGER.error("cannot get auditlog", e);
        }
        finally {
            mySafeClose(resultSet);
            mySafeClose(connection);
        }
        return Collections.unmodifiableList(retval);
    }

    /**
     *
     * @param property the property to persist
     * @return return true on success, false otherwise
     */
    public boolean createNewProperty(final Property property) {
        Connection connection = null;
        if(null == property) return false;

        final String uuid =property.getUuid();
        final String author = property.getAuthor();
        final String comment = property.getComments();
        final String propertyName = property.getPropertyName();
        final String context = property.getContext();
        final String value = property.getValue();

        if(isBlank(uuid) || isBlank(author) || isBlank(comment) || isBlank(propertyName) || isBlank(value)) {
            return false;
        }

        final long now = System.currentTimeMillis();
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken.");
                return false;
            }
            connection.createStatement().execute("START TRANSACTION");
            final String insert = "INSERT INTO properties(" +
                    "uuid, propertyName, context, value, author, comments, lastModified) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)";
            final PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, propertyName);
            preparedStatement.setString(3, context);
            preparedStatement.setString(4, value);
            preparedStatement.setString(5, author);
            preparedStatement.setString(6, comment);
            preparedStatement.setLong(7, now);
            preparedStatement.executeUpdate();
            insertIntoAuditLog("create", author, comment, new Property(), property, now, connection);
            connection.commit();
        }
        catch(SQLException e) {
            LOGGER.error("cannot create new property", e);
            mySafeRollback(connection);
            return false;
        }
        finally {
            mySafeClose(connection);
        }
        return true;
    }

    private void insertIntoAuditLog(final String action,
                                    final String author,
                                    final String comment,
                                    final Property oldProperty,
                                    final Property newProperty,
                                    final long now,
                                    Connection connection) throws SQLException {
        final Date oldLastModified = oldProperty.getLastModified();
        final long oldModificationTime = ((null == oldLastModified) ? 0 : oldLastModified.getTime());
        final Date newLastModified = newProperty.getLastModified();
        final long newModificationTime = ((null == newLastModified) ? 0 : newLastModified.getTime());
        final String insert = "INSERT INTO auditlog(timestamp, action, author, comment, " +
                "olduuid, oldname, oldcontext, oldvalue, oldauthor, oldcomments, oldLastModified, " +
                "newuuid, newname, newcontext, newvalue, newauthor, newcomments, newLastModified) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insert);
        preparedStatement.setLong(1, now);
        preparedStatement.setString(2, action);
        preparedStatement.setString(3, author);
        preparedStatement.setString(4, comment);
        preparedStatement.setString(5, oldProperty.getUuid());
        preparedStatement.setString(6, oldProperty.getPropertyName());
        preparedStatement.setString(7, oldProperty.getContext());
        preparedStatement.setString(8, oldProperty.getValue());
        preparedStatement.setString(9, oldProperty.getAuthor());
        preparedStatement.setString(10, oldProperty.getComments());
        preparedStatement.setLong(11, oldModificationTime);
        preparedStatement.setString(12, newProperty.getUuid());
        preparedStatement.setString(13, newProperty.getPropertyName());
        preparedStatement.setString(14, newProperty.getContext());
        preparedStatement.setString(15, newProperty.getValue());
        preparedStatement.setString(16, newProperty.getAuthor());
        preparedStatement.setString(17, newProperty.getComments());
        preparedStatement.setLong(18, newModificationTime);
        preparedStatement.executeUpdate();
    }

    /**
     *
     * @param oldProperty the property to edit
     * @param newProperty the propert to persist
     * @return true on success, false otherwise
     */
    public boolean editProperty(final Property oldProperty, final Property newProperty) {
        if(null == oldProperty || null == newProperty) return false;
        final String oldUUID = oldProperty.getUuid();
        final String newUUID = newProperty.getUuid();
        final String author = newProperty.getAuthor();
        final String comment = newProperty.getComments();
        final long now = System.currentTimeMillis();

        if(isBlank(oldUUID) || isBlank(newUUID) || isBlank(author) || isBlank(comment) || !oldUUID.equals(newUUID)) {
            return false;
        }

        final String newPropertyName = newProperty.getPropertyName();
        final String newContext = newProperty.getContext();
        final String newValue = newProperty.getValue();

        if(isBlank(newPropertyName) || isBlank(newValue)) return false;

        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
              LOGGER.error("cannot connect to DB with a null connection. persistence is broken.");
              return false;
            }
            connection.createStatement().execute("START TRANSACTION");
            final String update = "UPDATE properties SET propertyName=?," +
                    "context=?, value=?, author=?, comments=?, lastModified=? " +
                    "WHERE uuid=?";
            final PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, newPropertyName);
            preparedStatement.setString(2, newContext);
            preparedStatement.setString(3, newValue);
            preparedStatement.setString(4, author);
            preparedStatement.setString(5, comment);
            preparedStatement.setLong(6, now);
            preparedStatement.setString(7, newUUID);
            preparedStatement.executeUpdate();
            insertIntoAuditLog("edit", author, comment, oldProperty, newProperty, now, connection);
            connection.commit();
        }
        catch(SQLException e) {
            LOGGER.error("cannot edit property", e);
            mySafeRollback(connection);
            return false;
        }
        finally {
            mySafeClose(connection);
        }
        return true;
    }

    /**
     *
     * @param author the person deleting the property
     * @param comment why author is deleting the property
     * @param oldProperty the property to remove from persistence
     * @return true on success, false otherwise
     */
    public boolean deleteProperty(final String author, final String comment, final Property oldProperty) {
        if(null == oldProperty) return false;

        final long now = System.currentTimeMillis();
        final String uuid = oldProperty.getUuid();

        if(isBlank(author) || isBlank(comment) || isBlank(uuid)) return false;

        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken.");
                return false;
            }
            connection.createStatement().execute("START TRANSACTION");
            final String delete = "DELETE FROM properties where uuid=?";
            final PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setString(1, uuid);
            preparedStatement.executeUpdate();
            insertIntoAuditLog("delete", author, comment, oldProperty, new Property(), now, connection);
            connection.commit();
        }
        catch(SQLException e) {
            LOGGER.error("could not delete property with uuid {}", uuid, e);
            mySafeRollback(connection);
            return false;
        }
        finally {
            mySafeClose(connection);
        }
        return true;
    }

    public Property getProperty(final String uuid) {
        Property retval = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken. returning null property");
                return null;
            }
            final String select = "SELECT uuid, propertyName, context, value, author, comments, lastModified FROM properties where uuid=?";
            final PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setString(1, uuid);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final String propertyName = resultSet.getString("propertyName");
                final String context = resultSet.getString("context");
                final String value = resultSet.getString("value");
                final String author = resultSet.getString("author");
                final String comments = resultSet.getString("comments");
                final long lastModified = resultSet.getLong("lastModified");
                retval = new Property(uuid, propertyName, context, value, author, comments, lastModified);
                break; // Potential BUG: maybe we should count and error out instead of just returning the first one.
            }
        }
        catch(SQLException e) {
            LOGGER.error("could not get property with uuid {}", uuid, e);
        }
        finally {
            mySafeClose(resultSet);
            mySafeClose(connection);
        }
        return retval;
    }

    public List<Property> searchForProperty(final String pattern) {
        List<Property> retval = new ArrayList<Property>();
        ResultSet resultSet = null;
        final String select = "SELECT uuid, propertyName, context, value, author, comments, lastModified " +
                "FROM properties where propertyName LIKE ?";
        Connection connection = null;
        try {
            connection = locator.getDBConnection();
            if(null == connection) {
                LOGGER.error("cannot connect to DB with a null connection. persistence is broken. returning an empty list.");
                return Collections.emptyList();
            }
            final PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setString(1, "%" + pattern + "%");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final String uuid = resultSet.getString("uuid");
                final String propertyName = resultSet.getString("propertyName");
                final String context = resultSet.getString("context");
                final String value = resultSet.getString("value");
                final String author = resultSet.getString("author");
                final String comments = resultSet.getString("comments");
                final long lastModified = resultSet.getLong("lastModified");
                final Property property = new Property(uuid, propertyName, context, value, author, comments, lastModified);
                retval.add(property);
            }
        }
        catch(SQLException e) {
            LOGGER.error("cannot search for property with pattern {}", pattern, e);
        }
        finally {
            mySafeClose(resultSet);
            mySafeClose(connection);
        }
        return Collections.unmodifiableList(retval);
    }

    private void mySafeClose(final Connection connection) {
        if(null != connection) {
            try {
                connection.close();
            }
            catch(SQLException e) {
                LOGGER.error("could not close the connection");
            }
        }
    }

    private void mySafeRollback(final Connection connection) {
        if(null != connection) {
            try {
                connection.rollback();
            }
            catch(SQLException e) {
                LOGGER.error("could not roll back the connection");
            }
        }
    }

    private void mySafeClose(final ResultSet resultSet) {
        if(null != resultSet) {
            try {
                resultSet.close();
            }
            catch(SQLException e) {
                LOGGER.error("could not close the resultSet");
            }
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    connection = locator.getDBConnection();
                    if(null == connection) {
                        LOGGER.error("cannot connect to DB with a null connection. persistence is broken. cannot shutdown cleanly.");
                        return;
                    }
                    connection.createStatement().executeUpdate("SHUTDOWN COMPACT");
                }
                catch(SQLException e) {
                    LOGGER.error("had trouble shutting down cleanly", e);
                }
                finally {
                    mySafeClose(connection);
                }
            }
        }));
    }

    private static boolean isBlank(final String s) {
        return (null == s || "".equals(s));
    }

}
