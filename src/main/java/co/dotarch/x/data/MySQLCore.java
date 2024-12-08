package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;
import org.bukkit.plugin.Plugin;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MySQLCore extends AbstractDatabaseCore {

    private static final int MAX_CONNECTIONS = 8;
    private final List<DatabaseConnection> POOL = new ArrayList<>();
    /**
     * The connection properties... user, pass, autoReconnect..
     */
    private final Properties info;
    private final String url;
    private final DotX plugin;

    public MySQLCore(
            DotX plugin,
             String host,
             String user,
             String pass,
             String database,
             String port,
            boolean useSSL) {
        this.plugin = plugin;
        info = new Properties();
        info.setProperty("autoReconnect", "true");
        info.setProperty("user", user);
        info.setProperty("password", pass);
        info.setProperty("useUnicode", "true");
        info.setProperty("characterEncoding", "utf8");
        info.setProperty("maxReconnects", "65535");
        info.setProperty("failOverReadOnly", "false");
        info.setProperty("useSSL", String.valueOf(useSSL));
        if (false) { //TODO Option for addBatch to improve performance
            info.setProperty("rewriteBatchedStatements", "true");
        }
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            POOL.add(null);
        }
    }

    @Override
    synchronized void close() {
        for (DatabaseConnection databaseConnection : POOL) {
            if (databaseConnection == null || !databaseConnection.isValid()) {
                continue;
            }
            if (!databaseConnection.isUsing()) {
                databaseConnection.close();
            } else {
                //Wait until the connection is finished
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                } finally {
                    close();
                }
            }
        }
    }

    synchronized protected DatabaseConnection getConnection0() {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            DatabaseConnection connection = POOL.get(i);
            // If we have a current connection, fetch it
            if (connection == null) {
                return genConnection(i);
            } else if (!connection.isUsing()) {
                if (connection.isValid()) {
                    return connection;
                } else {
                    // Else, it is invalid, return another connection.
                    connection.close();
                    return genConnection(i);
                }
            }

        }
        //If all connection is unusable, wait a moment
        waitForConnection();
        return getConnection0();
    }

    synchronized private DatabaseConnection genConnection(int index) {
        try {
            DatabaseConnection connection = new DatabaseConnection(this, DriverManager.getConnection(this.url, info));
            POOL.set(index, connection);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create a new connection", e);
        }
    }

    @Override
    public String getName() {
        return "BuiltIn-MySQL";
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
