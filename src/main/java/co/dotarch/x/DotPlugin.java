package co.dotarch.x;

import co.dotarch.x.config.VirtualizedConfiguration;
import co.dotarch.x.data.DotPlayer;
import co.dotarch.x.events.PlayerLoadedEvent;
import co.dotarch.x.messages.MessageTemplate;
import co.dotarch.x.messages.Messages;
import co.dotarch.x.plugin.DotX;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Parent class for any JavaPlugin that wants to use DotX features.
 */
public abstract class DotPlugin extends JavaPlugin implements Listener
{
    public DotPlugin()
    {
        super();
    }

    protected DotPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    private File messagesFile;
    private FileConfiguration messagesConfiguration;
    private Messages messages;
    protected final HashMap<String, VirtualizedConfiguration> virtualConfigs = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesConfig();
        messages = new Messages(messagesConfiguration);

        // Load vConfigs
        for(String filename : getConfigFilenames())
        {
            var vConfigFile = new File(getDataFolder(), filename);
            if (!vConfigFile.exists()) {
                vConfigFile.getParentFile().mkdirs();
                saveResource(filename, false);
            }

            var vConfigUnderlying = new YamlConfiguration();
            try {
                vConfigUnderlying.load(vConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            virtualConfigs.put(filename, new VirtualizedConfiguration(
                    vConfigUnderlying,
                    filename,
                    this
            ));
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DotX]--\uD83D\uDD17--[" + this.getName());
    }

    @Override
    public void onDisable()
    {

    }

    /**
     * Allows quick resolution of config filenames on a per-class basis,
      * @param clazz The class you're calling the method from
     * @return The appropriate config file for that class, as you've defined.
     */
    public abstract String getConfigFilenameForClass(Class clazz);

    /**
     * Get all config filenames
     * @return
     */
    public abstract String[] getConfigFilenames();

    /**
     * Get virtual configuration
     * @param filename Filename for which to retrive vConfig
     * @return vConfig
     */
    public VirtualizedConfiguration getVConfig(String filename)
    {
        return virtualConfigs.get(filename);
    }

    /**
     * Dynamically update a message template
     * @param key Key at which to update the message
     * @param newTemplate The new message template
     */
    public void injectMessageUpdate(String key, MessageTemplate newTemplate)
    {
        messages.inject(key, newTemplate);
    }

    /**
     * Get messages
     * @return Messages instance for your plugin
     */
    public Messages messages()
    {
        return messages;
    }

    /**
     * Get the DotAPI plugin
     * @return the DotAPI plugin
     */
    public static DotX api()
    {
        return DotX.instance();
    }

    /**
     * Called whenever a player is loaded - use to set up initial values etc.
     * @param player
     */
    public abstract void handleLoadedPlayer(DotPlayer player);

    @EventHandler
    public void onDotPlayerLoaded(PlayerLoadedEvent e)
    {
        handleLoadedPlayer(e.getDotPlayer());
    }


    private void createMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfiguration = new YamlConfiguration();
        try {
            messagesConfiguration.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
