package dotarch.api.data;

import dotarch.api.DotX;
import dotarch.api.events.ObjectLoadedEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public record DotObject(ObjectCache cache, UUID uuid, String type, HashMap<String, String> properties)
{

    static final HashMap<String, String> initHashMap = new HashMap<String, String>();

    String serialize()
    {
        return DotX.instance().gson().toJson(properties);
    }

    static DotObject load(@NotNull UUID uuid, @NotNull String type, @NotNull ObjectCache cache)
    {
        return load(uuid, type, cache, initHashMap);
    }

    static DotObject load(@NotNull UUID uuid, @NotNull String type, @NotNull ObjectCache cache, @NotNull HashMap<String, String> initializationMapIfMissing)
    {
        var loadedDotObject = DotX.databaseHelper.readObject(uuid, type, cache, initializationMapIfMissing);
        ObjectLoadedEvent event = new ObjectLoadedEvent(loadedDotObject);
        Bukkit.getPluginManager().callEvent(event);
        return loadedDotObject;
    }

    public void save()
    {
        DotX.databaseHelper.updateObject(this);
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
