package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * A Util to execute all SQLs.
 */
public class DatabaseHelper {

	private final DatabaseManager manager;

	private final DotX plugin;


	private static final String USERS_TABLE = "users";
	private static final String OBJECTS_TABLE = "objects";
	private static final String BLOCKS_TABLE = "blocks";

	public DatabaseHelper(DotX plugin, DatabaseManager manager) throws SQLException {
		this.plugin = plugin;
		this.manager = manager;
		if (!manager.hasTable(plugin.getDbPrefix() + USERS_TABLE)) {
			createUsersTable();
		}
		if (!manager.hasTable(plugin.getDbPrefix() + OBJECTS_TABLE)) {
			createObjectsTable();
		}
		if (!manager.hasTable(plugin.getDbPrefix() + BLOCKS_TABLE)) {
			createBlocksTable();
		}
	}

	/**
	 * Creates the database table 'users'.
	 */

	private void createUsersTable() {
		String sqlString = "CREATE TABLE " + plugin.getDbPrefix() + USERS_TABLE
				+ " (uuid VARCHAR(255) NOT NULL, json JSON NOT NULL);";
		manager.runInstantTask(new DatabaseTask(sqlString));
	}

	public DotPlayer readUUID(Player player, PlayerCache cache)
	{
		String serialString = getUserIfExists(player.getUniqueId());
		if (!serialString.isEmpty())
		{
			return new DotPlayer(cache, player, player.getUniqueId(), DotX.instance().gson().fromJson(serialString, HashMap.class));
		}
		else
		{
			DotPlayer newPlayer = new DotPlayer(cache, player, player.getUniqueId(), DotPlayer.initHashMap);

			String sqlString = "INSERT INTO " + plugin.getDbPrefix() + USERS_TABLE + " (uuid, json) VALUES (?,?)";
			manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, newPlayer.serialize());
			}));

			return newPlayer;
		}
    }

	public void updateUser(DotPlayer player)
	{
		String sqlString = "UPDATE " + plugin.getDbPrefix() + USERS_TABLE + " SET json = ? WHERE uuid = ?";
		manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
			ps.setString(1, player.serialize());
			ps.setString(2, player.uuid().toString());
		}));
	}

	public String getUserIfExists(UUID player) {
		String value = "";
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + USERS_TABLE + " WHERE uuid = '"+player.toString()+"'";
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
		String sqlString = "CREATE TABLE " + plugin.getDbPrefix() + OBJECTS_TABLE
				+ " (uuid VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, json JSON NOT NULL);";
		manager.runInstantTask(new DatabaseTask(sqlString));
	}

	public DotObject readObject(@NonNull UUID uuid, @NonNull String type, @NonNull ObjectCache cache, @Nullable HashMap<String, String> initializeIfMissing)
	{
		String serialString = getObjectIfExists(uuid, type);
		if (!Objects.equals(serialString, ""))
		{
			return new DotObject(cache, uuid, type, DotX.instance().gson().fromJson(serialString, HashMap.class));
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

			String sqlString = "INSERT INTO " + plugin.getDbPrefix() + OBJECTS_TABLE + " (uuid, type, json) VALUES (?,?, ?)";
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
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + OBJECTS_TABLE + " WHERE type = '" + type +  "'";
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
								var decodedProperties = DotX.instance().gson().fromJson(serialString, HashMap.class);
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
		String sqlString = "UPDATE " + plugin.getDbPrefix() + OBJECTS_TABLE + " SET json = ? WHERE uuid = ?, type = ?";
		manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
			ps.setString(1, object.serialize());
			ps.setString(2, object.uuid().toString());
			ps.setString(3, object.type());
		}));
	}

	public String getObjectIfExists(UUID object, String type) {
		String value = "";
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + OBJECTS_TABLE + " WHERE uuid = '" + object.toString() + "' AND type = '" + type +  "'";
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
	 * DotBlock table + handling
	 */


	private void createBlocksTable() {
		String createTable = "CREATE TABLE " + plugin.getDbPrefix() + BLOCKS_TABLE
				+ " (uuid VARCHAR(100) NOT NULL, chunkKey TEXT NOT NULL, json JSON NOT NULL, blockChunkX INT NOT NULL, blockY INT NOT NULL, blockChunkZ INT NOT NULL, PRIMARY KEY (uuid));";
		manager.runInstantTask(new DatabaseTask(createTable));
		String addIndex = "CREATE INDEX idx_chunkKey\n" +
				"ON " + plugin.getDbPrefix() + BLOCKS_TABLE + " (chunkKey); ";
		manager.runInstantTask(new DatabaseTask(addIndex));
	}

	public ArrayList<DotBlock> tryReadChunk(Chunk chunk, BlockCache cache)
	{
		ArrayList<Pair<Pair<String, Block>, UUID>> serials = getBlockDataForChunk(chunk);
		ArrayList<DotBlock> returnable = new ArrayList<>();

		for (var serialPair : serials)
		{
			returnable.add(new DotBlock(cache, serialPair.getLeft().getRight(), serialPair.getRight(), DotX.instance().gson().fromJson(serialPair.getLeft().getLeft(), HashMap.class)));
		}

		return returnable;
	}

	public void createBlock(DotBlock block)
	{
		String sqlString = "INSERT INTO " + plugin.getDbPrefix() + OBJECTS_TABLE + " (uuid, chunkKey, json, blockChunkX, blockY, blockChunkZ) VALUES (?,?,?,?,?,?)";
		manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
			ps.setString(1, block.getUuid().toString());
			ps.setString(2, makeChunkKey(block.getBlock().getChunk()));
			ps.setString(3, block.serialize());
			ps.setInt(4, block.getBlock().getX() & 0xF);
			ps.setInt(5, block.getBlock().getY() & 0xFF);
			ps.setInt(5, block.getBlock().getZ() & 0xF);
		}));
	}

	public void updateBlock(DotBlock block)
	{
		String sqlString = "UPDATE " + plugin.getDbPrefix() + BLOCKS_TABLE + " SET json = ? WHERE uuid = ?";
		manager.addDelayTask(new DatabaseTask(sqlString, (ps) -> {
			ps.setString(1, block.serialize());
			ps.setString(2, block.getUuid().toString());
		}));
	}

	public void deleteBlock(DotBlock block)
	{
		String deleteQuery = "DELETE FROM " + plugin.getDbPrefix() + BLOCKS_TABLE + " WHERE uuid = ?";
		manager.addDelayTask(new DatabaseTask(deleteQuery, (ps) -> {
			ps.setString(1, block.getUuid().toString());
		}));
	}

	public ArrayList<Pair<Pair<String, Block>, UUID>> getBlockDataForChunk(Chunk chunk) {
		ArrayList<Pair<Pair<String, Block>, UUID>> toReturn = new ArrayList<>();
		String sqlString = "SELECT * FROM " + plugin.getDbPrefix() + BLOCKS_TABLE + " WHERE chunkKey = '" + makeChunkKey(chunk) + "'";
		DatabaseConnection connection = manager.getDatabase().getConnection();
		try {
			Statement statement = connection.get().createStatement();
			if (statement != null) {
				try {
					ResultSet results = statement.executeQuery(sqlString);
					if (results != null)
					{
						try {
							while (results.next())
							{
								var uuid = UUID.fromString(results.getString("uuid"));
								var blockX = results.getInt("blockChunkX"); // X coord within chunk
								var blockY = results.getInt("blockY"); // Y coord within chunk
								var blockZ = results.getInt("blockChunkZ"); // Z coord within chunk

								Block block = chunk.getBlock(blockX, blockY, blockZ);
								var serial = results.getString("json");
								toReturn.add(Pair.of(Pair.of(serial, block), uuid));
							}
						}
						catch (Exception resultSetException)
						{
							resultSetException.printStackTrace();
							results.close();
							statement.close();
							connection.release();
						}
						results.close();
					}
				}
				catch (Exception statmentExcption)
				{
					statmentExcption.printStackTrace();
					statement.close();
					connection.release();
				}
				statement.close();
			}
		}
		catch (Exception generalException)
		{
			generalException.printStackTrace();
		}

		connection.release();
		return toReturn;
	}

	public String makeChunkKey(Chunk key)
	{
		return key.getX() + "_" + key.getZ() + "_" + key.getWorld();
	}


	private WarpedResultSet selectTable(String table) throws SQLException {
		DatabaseConnection databaseConnection = manager.getDatabase().getConnection();
		Statement st = databaseConnection.get().createStatement();
		String selectAllTaxes = "SELECT * FROM " + plugin.getDbPrefix() + table;
		ResultSet resultSet = st.executeQuery(selectAllTaxes);
		return new WarpedResultSet(st, resultSet, databaseConnection);
	}



}
