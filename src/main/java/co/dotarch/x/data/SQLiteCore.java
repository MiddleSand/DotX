package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteCore extends AbstractDatabaseCore {
    private final File dbFile;

    private final DotX plugin;
    private DatabaseConnection connection;

    public SQLiteCore(DotX plugin, File dbFile) {
        this.plugin = plugin;
        this.dbFile = dbFile;
    }

    @Override
    synchronized void close() {
        if (!connection.isUsing()) {
            if (connection != null && !connection.isValid()) {
                connection.close();
            }
        } else {
            //Wait until the connection is finished
            waitForConnection();
            close();
        }
    }


    @Override
    synchronized protected DatabaseConnection getConnection0() {
        if (this.connection == null) {
            return connection = genConnection();
        }
        // If we have a current connection, fetch it
        if (!this.connection.isUsing()) {
            if (connection.isValid()) {
                return this.connection;
            } else {
                connection.close();
                return connection = genConnection();
            }
        }
        //If all connection is unusable, wait a moment
        waitForConnection();
        return getConnection0();
    }

    synchronized private DatabaseConnection genConnection() {
        if (this.dbFile.exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                this.connection = new DatabaseConnection(this, DriverManager.getConnection("jdbc:sqlite:" + this.dbFile));
                return this.connection;
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Sqlite driver is not found", e);
            } catch (SQLException e) {
                throw new IllegalStateException("Start sqlite database connection failed", e);
            }
        } else {
            // So we need a new file.
            try {
                // Create the file
                //noinspection ResultOfMethodCallIgnored
                this.dbFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Sqlite database file create failed", e);
            }
            // Now we won't need a new file, just a connection.
            // This will return that new connection.
            return this.genConnection();
        }
    }

    @Override
    public String getName() {
        return "BuiltIn-SQLite";
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
