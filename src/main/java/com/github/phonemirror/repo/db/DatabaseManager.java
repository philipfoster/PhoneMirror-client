package com.github.phonemirror.repo.db;

import com.github.phonemirror.repo.KeyValueStore;
import com.github.phonemirror.repo.db.dao.DeviceDao;
import com.github.phonemirror.util.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class will create a sqlite database if it does not exist. It also manages {@link Connection}s to the
 * database.
 */
@Singleton
public class DatabaseManager {

    private static final Logger logger = Logger.getLogger(DatabaseManager.class);
    private static final String DB_IS_SETUP_KEY = "db.isSetup";
    private static final String DB_VERSION_KEY = "db.version";
    private static final String DB_URL_FMT = "jdbc:sqlite:" + System.getProperty("user.home") + "/.PhoneMirror/%s";
    private static final int DB_SCHEMA_VERSION = 1;

    private String dbUrl;
    private DataSource connectionPool;

    private KeyValueStore kvStore;


    @Inject
    public DatabaseManager(KeyValueStore kvStore, Configuration config) {
        this.kvStore = kvStore;
        dbUrl = String.format(DB_URL_FMT, config.getDbName());

        setupDatabase();
        setupConnectionPool();
        try {
            createTables();
        } catch (SQLException e) {
            logger.error("Could not create tables.");
            throw new RuntimeException("Could not create tables.", e);
        }
    }

    private void createTables() throws SQLException {
        DeviceDao.createTable(this);
    }

    private void setupConnectionPool() {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setJdbcUrl(dbUrl);
        // these can be tuned as necessary. Maybe add them as entries in config files?
        cpds.setMinPoolSize(2);
        cpds.setAutoCommitOnClose(true);
        cpds.setAcquireIncrement(2);
        cpds.setMaxPoolSize(10);

        connectionPool = cpds;
    }

    private void setupDatabase() {
        if (kvStore.containsKey(DB_IS_SETUP_KEY)) {
            return;
        }

        try (Connection ignored = DriverManager.getConnection(dbUrl)) {
            kvStore.set(DB_IS_SETUP_KEY, "true");
            kvStore.set(DB_VERSION_KEY, String.valueOf(DB_SCHEMA_VERSION));

        } catch (SQLException e) {
            logger.error("Could not create database.", e);
            throw new RuntimeException("Could not create database.");
        }
    }

    /**
     * @return A connection to the database to run queries.
     * @throws SQLException if a connection could not be obtained.
     */
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }


}
