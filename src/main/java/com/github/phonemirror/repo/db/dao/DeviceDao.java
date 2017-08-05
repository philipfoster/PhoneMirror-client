package com.github.phonemirror.repo.db.dao;

import com.github.phonemirror.pojo.Device;
import com.github.phonemirror.repo.db.DatabaseManager;

import javax.inject.Inject;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class acts as a middleman between the database and a {@link Device} object
 */
public class DeviceDao {

    private static final String TABLE_NAME = "Device";
    private static final String COL_PRIMARY_KEY = "id";
    private static final String COL_NAME = "Name";
    private static final String COL_SERIAL = "Serial";
    private static final String COL_PUBLIC_KEY = "PublicKey";

    private DatabaseManager dbMgr;

    @Inject
    public DeviceDao(DatabaseManager dbMgr) {
        this.dbMgr = dbMgr;
    }

    /**
     * Execute a sql CREATE TABLE statement to initialize the database.
     * @param dbMgr a reference to the database manager.
     * @throws SQLException if a problem occurred while creating the table
     */
    public static void createTable(DatabaseManager dbMgr) throws SQLException {
        System.out.println("Creating table for device");
        try (Connection conn = dbMgr.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                            COL_PRIMARY_KEY + " INTEGER PRIMARY KEY," +
                            COL_NAME + " TEXT UNIQUE, " +
                            COL_SERIAL + " CHAR(36), " +
                            COL_PUBLIC_KEY + " BLOB);"
            );
            stmt.execute();
        }
    }

    /**
     * Insert a {@link Device} into the table and update the device object's id field.
     * @param device the device to insert
     * @return the primary key of the newly inserted device. if an exception occurs, this will be -1, and should be ignored.
     * @throws SQLException if a db error occurs
     */
    public int insert(Device device) throws SQLException {
        Connection conn = null;
        int id = -1;
        try {
            conn = dbMgr.getConnection();
            
            String sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?);", TABLE_NAME, 
                    COL_NAME, COL_SERIAL, COL_PUBLIC_KEY);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, device.getName());
            stmt.setString(2, device.getSerialNo());
            stmt.setBlob(3, new SerialBlob(device.getEncryptionKey()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(COL_PRIMARY_KEY);
                device.setId(id);
            }
            
            conn.commit();
            
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        
        return id;
    }
    
    
    
}
