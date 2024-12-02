package dotarch.api;

import com.google.gson.Gson;
import dotarch.api.commands.DotAPICommand;
import dotarch.api.commands.VConfigCommand;
import dotarch.api.data.*;
import dotarch.api.web.WebserverThread;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.logging.Level;

public class DotAPI extends DotPlugin {

	private static DotAPI self;

	// Helpers
	private Gson gson;
	private PlayerCache playerCache;
	private ObjectCache objectCache;

	// Integrations
	private MiniMessage miniMessage;

	// Database
	public String dbPrefix = "dotapi";
	public static MySQLCore database;
	public static DatabaseManager databaseManager;
	public static DatabaseHelper databaseHelper;

	// Web
	private WebserverThread web;

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
		//objectCache = new ObjectCache();
		miniMessage = MiniMessage.miniMessage();
		web = new WebserverThread(this);

		getServer().getPluginManager().registerEvents(playerCache, this);
		getServer().getPluginManager().registerEvents(web, this);
		this.getCommand("dotapi").setExecutor(new DotAPICommand());
		this.getCommand("vconfig").setExecutor(new VConfigCommand());
		getLogger().info("DotX API plugin enabled");
	}

	@Override
	public void onDisable() {
		playerCache.saveAll();
		//objectCache.saveAll();
		getLogger().info("DotAPI disabled");
		self=null;
	}

	@Override
	public void fullReload()
	{

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

	public static DotAPI instance()
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

}
