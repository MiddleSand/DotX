package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;
import co.dotarch.x.events.PlayerLoadedEvent;
import co.dotarch.x.ui.InvMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Stack;
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

    private final Stack<InvMenu> menuStack = new Stack<>();

    public String serialize()
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

    /**
     * (Internal) Write the user's state to DB - you shouldn't need to do this.
     */
    public void save()
    {
        DotX.databaseHelper.updateUser(this);
    }

    /**
     * Check if a given property key exists.
     * @param key Key to lookup.
     * @return True if a matching key exists, false otherwise.
     */
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
        boolean existedAlready = hasProperty(key);
        if(!existedAlready) writeProperty(key, value, saveToDB);
        return existedAlready;
    }

    /**
     * Gets a property.
     * @param key Key to access the value at.
     * @param defaultValue Default value if the value isn't present in properties. Use as a just-in-case.
     * @return Value, or default if missing.
     * @return Value, or default if missing.
     */
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
     * Removes property, allowing you to decide to save
     * @param key string key
     */
    public String removeProperty(String key)
    {
        return removeProperty(key, true);
    }

    /**
     * Removes property, allowing you to decide to save
     * @param key string key
     * @param save if true, persists to DB
     */
    public String removeProperty(String key, boolean save)
    {
        var er = properties.remove(key);
        if(save)
        {
            save();
        }
        return er;
    }

    /**
     * Gets a transient property.
     * @param key Key to access the value at.
     * @param defaultValue Default value if the value isn't present in the transient properties. Use as a just-in-case.
     * @return Value, or default if missing.
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

    /**
     * Writes a transient value.
     * @param key Key to write the value at.
     * @param store Value to store.
     */
    public void writeTransient(String key, String store)
    {
        transientProperties.put(key, store);
    }

    /**
     * Check if a key exists within the transient properties map.
     * @param key Key to lookup.
     * @return True if exists, false otherwise.
     */
    public boolean hasTransient(String key)
    {
        return transientProperties.containsKey(key);
    }

    /**
     * Removes property, allowing you to decide to save
     * @param key string key
     */
    public String removeTransient(String key)
    {
        return transientProperties.remove(key);
    }

    /**
     * Navigate the player to a menu.
     * @param menu The menu to navigate to.
     * @return True if the menu could accommodate the player and has been displayed, false otherwise
     */
    public boolean navigateTo(InvMenu menu)
    {
        if(menu.canAccept(this))
        {
            menuStack.push(menu);
            menu.land(this);
            return true;
        }
        return false;

    }

    /**
     * Navigates to the previously-accessed menu.
     * @return True if there was a menu to leave, false otherwise.
     */
    public boolean navigateBack()
    {
        if(!menuStack.isEmpty())
        {
            menuStack.pop().back(this);
            this.player.closeInventory();
            if(!menuStack.isEmpty())
            {
                menuStack.peek().land(this);
            }
            return true;
        }
        return false;
    }
}
