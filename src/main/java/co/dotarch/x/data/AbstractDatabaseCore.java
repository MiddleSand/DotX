package co.dotarch.x.data;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public abstract class AbstractDatabaseCore {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition conditionLock = lock.newCondition();

    void waitForConnection() {
        try {
            lock.lock();
            conditionLock.await();
            lock.unlock();
        } catch (InterruptedException e) {
            getPlugin().getLogger().log(Level.SEVERE, "Exception when waiting new database connection", e);
            Thread.currentThread().interrupt();
        }
    }

    void signalForNewConnection() {
        lock.lock();
        conditionLock.signal();
        lock.unlock();
    }

    abstract void close();

    /**
     * Gets the database connection for executing queries on.
     *
     * @return The database connection, PLEASE MAKE SURE USING DatabaseConnection#release to CLOSE THE CONNECTION
     */
    synchronized DatabaseConnection getConnection() {
        DatabaseConnection databaseConnection = getConnection0();
        databaseConnection.markUsing();
        return databaseConnection;
    }

    abstract protected DatabaseConnection getConnection0();

    abstract public String getName();

    abstract public Plugin getPlugin();

}
