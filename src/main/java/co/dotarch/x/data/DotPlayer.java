package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;
import co.dotarch.x.events.PlayerLoadedEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
@RequiredArgsConstructor
public class DotPlayer
{
    private final PlayerCache cache;
    public PlayerCache cache()
    {
        return cache;
    }

    private final Player player;
    public Player player()
    {
        return player;
    }

    private final UUID uuid;
    public UUID uuid()
    {
        return uuid;
    }

    private final HashMap<String, String> properties;

    static final HashMap<String, String> initHashMap = new HashMap<String, String>();
    private final HashMap<String, String> transientProperties = new HashMap<String, String>();

    String serialize()
    {
        return DotX.instance().gson().toJson(properties);
    }

    static DotPlayer load(@NotNull Player player, @NotNull PlayerCache cache)
    {
        var loadedDotPlayer = DotX.databaseHelper.readUUID(player, cache);
        PlayerLoadedEvent event = new PlayerLoadedEvent(player, loadedDotPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return loadedDotPlayer;
    }

    public void save()
    {
        DotX.databaseHelper.updateUser(this);
    }

    public boolean hasProperty(String key)
    {
        return properties.containsKey(key);
    }

    /**
     * Initializes property if it doesn't exist anymore
     * @param key
     * @param value
     * @return True if initialized, false if already existed
     */
    public boolean initializeProperty(String key, String value, boolean saveToDB)
    {
        boolean existedAlready = !hasProperty(key);
        if(!existedAlready) writeProperty(key, value, saveToDB);
        return existedAlready;
    }

    public String getProperty(String key, String defaultValue)
    {
        var returnable = properties.get(key);
        if(returnable == null)
        {
            DotX.api().getLogger().warning("Tracked property '" + key + "' of DotPlayer '" + player.getName() + "' is null upon read...");
            return defaultValue;
        }
        return properties.get(key);
    }

    /**
     * Writes property, but does not save yet
     * @param key string key
     * @param value serializable value
     */
    public void writeProperty(String key, String value)
    {
        writeProperty(key, value,false);
    }

    /**
     * Writes property, allowing you to decide to save
     * @param key string key
     * @param value serializable value
     * @param save if true, persists to DB
     */
    public void writeProperty(String key, String value, boolean save)
    {
        properties.put(key, value);
        if(save)
        {
            save();
        }
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Object getTransient(String key, String defaultValue)
    {
        var returnable = transientProperties.get(key);
        if(returnable == null)
        {
            DotX.api().getLogger().warning("Transient property '" + key + "' of DotPlayer '" + player.getName() + "' is null upon read...");
            return defaultValue;
        }
        return transientProperties.get(key);
    }

    public void writeTransient(String key, String store)
    {
        transientProperties.put(key, store);
    }

    public boolean hasTransient(String key)
    {
        return transientProperties.containsKey(key);
    }
}
