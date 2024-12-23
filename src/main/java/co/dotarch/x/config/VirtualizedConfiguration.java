package co.dotarch.x.config;

import co.dotarch.x.DotPlugin;
import co.dotarch.x.events.VConfigurationEvent;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class VirtualizedConfiguration
{
    @Getter
    private final YamlConfiguration underlying;
    @Getter
    private final String filename;
    @Getter
    private final DotPlugin plugin;
    private final HashMap<String, VirtualConfigEntry> entries = new HashMap<>();

    public VirtualizedConfiguration(YamlConfiguration underlying, String filename, DotPlugin plugin)
    {
        this.underlying = underlying;
        this.filename = filename;
        this.plugin = plugin;
        reload(false);
    }

    @NonNull
    public VirtualConfigEntry getEntry(String key)
    {
        var retrieved = this.entries.get(key);
        if(retrieved == null)
        {
            throw new NullPointerException(
                    "Configuration key " + key + " does not exist, please consult file " + filename + " of plugin " + plugin.getName() + " and fix your mistakes."
            );
        }
        return retrieved;
    }


    /**
     * Reloads config from stored file.
     */
    public void reload(boolean expectingPrevious)
    {
        // == Don't clear because otherwise hot reload refs break!! == // this.entries.clear();
        var changes = new HashMap<String, Pair<Object, Object>>();
        for (String key : getUnderlying().getKeys(true)) {
            VirtualConfigEntry newVal;
            VirtualConfigEntry oldVal;
            if(!expectingPrevious)
            {
                newVal = new VirtualConfigEntry(key, getUnderlying().get(key), this);
                entries.put(key, newVal);
                oldVal = null;
            }
            else
            {
                newVal = entries.get(key);
                oldVal = new VirtualConfigEntry(key, newVal.getValue(), this);
                // the new value ref doesnt actually ref the new value until this line
                newVal.setValue(getUnderlying().get(key));
            }
            if(oldVal == null || !oldVal.getValue().equals(newVal.getValue()))
            {
                // Only publish changes if either the value is new or the old value is different from the new value
                changes.put(key, Pair.of(oldVal, newVal));
            }
        }
        if(!changes.isEmpty())
        {
            VConfigurationEvent e = new VConfigurationEvent(changes, getUnderlying().getName(), this.plugin);
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    /**
     * Reloads config from stored file.
     */
    public void reload()
    {
        reload(true);
    }
}
