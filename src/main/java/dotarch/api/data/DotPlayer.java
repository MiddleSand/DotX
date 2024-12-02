package dotarch.api.data;

import dotarch.api.DotAPI;
import dotarch.api.events.PlayerLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public record DotPlayer(PlayerCache cache, Player player, UUID uuid, HashMap<String, String> properties)
{
    static final HashMap<String, String> initHashMap = new HashMap<String, String>();

    String serialize()
    {
        return DotAPI.instance().gson().toJson(properties);
    }

    static DotPlayer load(@NotNull Player player, @NotNull PlayerCache cache)
    {
        var loadedDotPlayer = DotAPI.databaseHelper.readUUID(player, cache);
        PlayerLoadedEvent event = new PlayerLoadedEvent(player, loadedDotPlayer);
        Bukkit.getPluginManager().callEvent(event);
        return loadedDotPlayer;
    }

    public void save()
    {
        DotAPI.databaseHelper.updateUser(this);
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
        return properties.getOrDefault(key, defaultValue);
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
}
