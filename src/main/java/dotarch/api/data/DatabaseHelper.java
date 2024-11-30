package dotarch.api.data;

import dotarch.api.DotAPI;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * A Util to execute all SQLs.
 */
public class DatabaseHelper {

	private final DatabaseManager manager;

	private final DotAPI plugin;

	public DatabaseHelper(DotAPI plugin, DatabaseManager manager) throws SQLException {
		this.plugin = plugin;
		this.manager = manager;
		if (!manager.hasTable(plugin.getDbPrefix() + "users")) {
			createUsersTable();
		}
		if (!manager.hasTable(plugin.getDbPrefix() + "objects")) {
			createObjectsTable();
		}
	}

	/**
	 * Creates the database table 'users'.
	 */

	private void createUsersTable() {
		String sqlString = "CREATE TABLE " + plugin.getDbPrefix()
				+ "users (uuid VARCHAR(255) NOT NULL, json JSON NOT NULL);";
		manager.runInstantTask(new DatabaseTask(sqlString));
	}

	public DotPlayer readUUID(Player player, PlayerCache cache)
	{
		String serialString = getUserIfExists(player.getUniqueId());
		if (!Objects.equals(serialString, ""))
		{
			return new DotPlayer(cache, player, player.getUniqueId(), DotAPI.getInstance().gson().fromJson(serialString, HashMap.class));
		}
		else
		{
			DotPlayer newPlayer = new DotPlayer(cache, player, player.getUniqueId(), DotPlayer.initHashMap);

			String sqlString = "INSERT INTO " + plugin.getDbPrefix() + "users (uuid, json) VALUES (?,?)";
			manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, newPlayer.serialize());
			}));

			return newPlayer;
		}
    }

	public void updateUser(DotPlayer player)
	{
		String sqlString = "UPDATE " + plugin.getDbPrefix() + "users SET json = ? WHERE uuid = ?";
		manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
			ps.setString(1, player.serialize());
			ps.setString(2, player.uuid().toString());
		}));
	}

	public String getUserIfExists(UUID player) {
		String value = "";
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + "users WHERE uuid = '"+player.toString()+"'";
		DatabaseConnection connection = manager.getDatabase().getConnection();
		try {
			Statement statement = connection.get().createStatement();
			if (statement != null) {
				try {
					ResultSet results = statement.executeQuery(sqlString);
					if (results != null) {
						try {
							if (results.next()!= false) {
								value = results.getString("json");
							}
						} catch (Exception resultSetException) {
							resultSetException.printStackTrace();
						}
						results.close();
					}
				} catch (Exception statmentExcption) {
					statmentExcption.printStackTrace();
				}
				statement.close();
			}
		} catch (Exception generalException) {
			generalException.printStackTrace();
		}

		connection.release();
		return value;
	}

	/**
	 * Table for objects
	 */


	private void createObjectsTable() {
		String sqlString = "CREATE TABLE " + plugin.getDbPrefix()
				+ "objects (uuid VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, json JSON NOT NULL);";
		manager.runInstantTask(new DatabaseTask(sqlString));
	}

	public DotObject readObject(@NonNull UUID uuid, @NonNull String type, @NonNull ObjectCache cache, @Nullable HashMap<String, String> initializeIfMissing)
	{
		String serialString = getObjectIfExists(uuid, type);
		if (!Objects.equals(serialString, ""))
		{
			return new DotObject(cache, uuid, type, DotAPI.getInstance().gson().fromJson(serialString, HashMap.class));
		}
		else
		{
			HashMap<String, String> initializationMap;
			if(initializeIfMissing == null)
			{
				initializationMap = DotObject.initHashMap;
			}
			else
			{
				initializationMap = initializeIfMissing;
			}

			DotObject object = new DotObject(cache, uuid, type, initializationMap);

			String sqlString = "INSERT INTO " + plugin.getDbPrefix() + "objects (uuid, type, json) VALUES (?,?, ?)";
			manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
				ps.setString(1, uuid.toString());
				ps.setString(2, type);
				ps.setString(3, object.serialize());
			}));

			return object;
		}
	}

	public HashMap<UUID, DotObject> readType(@NonNull String type, @NonNull ObjectCache cache)
	{
		HashMap<UUID, DotObject> ret = new HashMap<>();
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + "users WHERE type = '" + type +  "'";
		DatabaseConnection connection = manager.getDatabase().getConnection();
		try {
			Statement statement = connection.get().createStatement();
			if (statement != null) {
				try {
					ResultSet results = statement.executeQuery(sqlString);
					if (results != null) {
						try {
							while(results.next())
							{
								var uuid = UUID.fromString(results.getString("uuid"));
								var serialString = results.getString("json");
								var decodedProperties = DotAPI.getInstance().gson().fromJson(serialString, HashMap.class);
								DotObject object = new DotObject(cache, uuid, type, decodedProperties);
								ret.put(uuid, object);
							}
						} catch (Exception resultSetException) {
							resultSetException.printStackTrace();
						}
						results.close();
					}
				} catch (Exception statmentExcption) {
					statmentExcption.printStackTrace();
				}
				statement.close();
			}
		} catch (Exception generalException) {
			generalException.printStackTrace();
		}

		connection.release();
		return ret;
	}

	public void updateObject(DotObject object)
	{
		String sqlString = "UPDATE " + plugin.getDbPrefix() + "objects SET json = ? WHERE uuid = ?, type = ?";
		manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
			ps.setString(1, object.serialize());
			ps.setString(2, object.uuid().toString());
			ps.setString(3, object.type());
		}));
	}

	public String getObjectIfExists(UUID object, String type) {
		String value = "";
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + "users WHERE uuid = '" + object.toString() + "' AND type = '" + type +  "'";
		DatabaseConnection connection = manager.getDatabase().getConnection();
		try {
			Statement statement = connection.get().createStatement();
			if (statement != null) {
				try {
					ResultSet results = statement.executeQuery(sqlString);
					if (results != null) {
						try {
							if (results.next()!= false) {
								value = results.getString("json");
							}
						} catch (Exception resultSetException) {
							resultSetException.printStackTrace();
						}
						results.close();
					}
				} catch (Exception statmentExcption) {
					statmentExcption.printStackTrace();
				}
				statement.close();
			}
		} catch (Exception generalException) {
			generalException.printStackTrace();
		}

		connection.release();
		return value;
	}

	private WarpedResultSet selectTable(String table) throws SQLException {
		DatabaseConnection databaseConnection = manager.getDatabase().getConnection();
		Statement st = databaseConnection.get().createStatement();
		String selectAllTaxes = "SELECT * FROM " + plugin.getDbPrefix() + table;
		ResultSet resultSet = st.executeQuery(selectAllTaxes);
		return new WarpedResultSet(st, resultSet, databaseConnection);
	}



}
