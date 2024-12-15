package co.dotarch.x.plugin;

import co.dotarch.x.commands.admin.ObjectCommand;
import co.dotarch.x.commands.admin.PlayerDataCommand;
import co.dotarch.x.data.*;
import com.google.gson.Gson;
import co.dotarch.x.DotPlugin;
import co.dotarch.x.FeaturePack;
import co.dotarch.x.commands.info.DotAPICommand;
import co.dotarch.x.commands.admin.VConfigCommand;
import co.dotarch.x.web.WebserverThread;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.logging.Level;

public class DotX extends DotPlugin
{
	private static DotX self;

	// Helpers
	private Gson gson;
	private PlayerCache playerCache;
	private ObjectCache objectCache;
	private BlockCache blockCache;
	@Getter
	private BukkitScheduler bukkitScheduler;

	// Integrations
	private MiniMessage miniMessage;

	// Database
	public String dbPrefix = "dotapi";
	public static MySQLCore database;
	public static DatabaseManager databaseManager;
	public static DatabaseHelper databaseHelper;

	// Web
	private WebserverThread web;

	// Featurepack Detection
	private final HashMap<String, FeaturePack> loadedFeaturePacks = new HashMap<>();

	@Override
	public void onEnable() {
		super.onEnable();
		self=this;
		gson = new Gson();
		try {
			setupDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		playerCache = new PlayerCache();
		objectCache = new ObjectCache();
		blockCache = new BlockCache();
		bukkitScheduler = Bukkit.getScheduler();
		miniMessage = MiniMessage.miniMessage();
		web = new WebserverThread(this);

		getServer().getPluginManager().registerEvents(playerCache, this);
		getServer().getPluginManager().registerEvents(blockCache, this);
		getServer().getPluginManager().registerEvents(web, this);
		this.getCommand("dotapi").setExecutor(new DotAPICommand());
		this.getCommand("vconfig").setExecutor(new VConfigCommand());
		var objectCommand = new ObjectCommand();
		this.getCommand("object").setExecutor(objectCommand);
		this.getCommand("object").setTabCompleter(objectCommand);
		var playerDataCommand = new PlayerDataCommand();
		this.getCommand("playerdata").setExecutor(playerDataCommand);
		this.getCommand("playerdata").setTabCompleter(playerDataCommand);
		getLogger().info("DotX API plugin enabled");
	}

	@Override
	public void onDisable() {
		playerCache.saveAll();
		//objectCache.saveAll();
		getLogger().info("DotAPI disabled");
		self=null;
	}

	public void restartWebserver()
	{
		this.web = new WebserverThread(this);
	}

	@Override
	public String getConfigFilenameForClass(Class clazz)
	{
		return "config.yml";
	}

	@Override
	public String[] getConfigFilenames()
	{
		return new String[]
				{
						"config.yml"
				};
	}

	public static DotX instance()
	{
		return self;
	}

	public Gson gson()
	{
		return instance().gson;
	}

	public PlayerCache players()
	{
		return instance().playerCache;
	}
	public ObjectCache objects() { return instance().objectCache; }
	public BlockCache blocks() { return instance().blockCache; }

	@Override
	public void handleLoadedPlayer(DotPlayer player)
	{
		// do nothing :)
	}

	public MiniMessage miniMessage()
	{
		return instance().miniMessage;
	}

	public String getDbPrefix()
	{
		return dbPrefix;
	}

	public DatabaseManager getDatabaseManager()
	{
		return databaseManager;
	}

	private void setupDatabase()
	{
		String connection = this.getConfig().getString("db.url");
		int port = this.getConfig().getInt("db.port");
		String username   = this.getConfig().getString("db.username");
		String password   = this.getConfig().getString("db.password");
		String database   = this.getConfig().getString("db.database");
		try {
			AbstractDatabaseCore dbCore;
			// SQLite database - Doing this handles file creation
			dbCore = new MySQLCore(this,
					connection,
					username,
					password,
					database,
					String.valueOf(port),
			false);
			databaseManager = new DatabaseManager(this, ServiceInjector.getDatabaseCore(dbCore));
			databaseHelper = new DatabaseHelper(this, databaseManager);
		} catch (DatabaseManager.ConnectionException e) {
			getLogger().log(Level.SEVERE, "Error when connecting to the database", e);
			getServer().getPluginManager().disablePlugin(this);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Error when setup database", e);
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void registerFeaturePack(FeaturePack pack)
	{
		loadedFeaturePacks.put(pack.getKey(), pack);
	}

	public boolean hasFeatures(String key)
	{
		return loadedFeaturePacks.containsKey(key);
	}

	public FeaturePack features(String key)
	{
		return loadedFeaturePacks.get(key);
	}

}
