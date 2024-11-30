package dotarch.api.config;

import dotarch.api.DotPlugin;
import dotarch.api.events.VConfigurationEvent;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;

import java.util.HashMap;

public class VirtualizedConfiguration
{
    @Getter
    private final Configuration underlying;
    @Getter
    private final DotPlugin plugin;
    private final HashMap<String, VirtualConfigEntry> entries = new HashMap<>();

    public VirtualizedConfiguration(Configuration underlying, DotPlugin plugin)
    {
        this.underlying = underlying;
        this.plugin = plugin;
        reload();
    }

    public VirtualConfigEntry getEntry(String key)
    {
        return this.entries.get(key);
    }


    /**
     * Reloads config from stored file.
     */
    public void reload()
    {
        // == Don't clear because otherwise hot reload refs break!! == // this.entries.clear();
        var changes = new HashMap<String, Pair<Object, Object>>();
        for (String key : getUnderlying().getKeys(false)) {
            if (getUnderlying().get(key) != this.entries.get(key).getValue())
            {
                var newVal = new VirtualConfigEntry(key, getUnderlying().get(key));
                var oldVal = this.entries.replace(key, newVal);
                changes.put(key, Pair.of(oldVal, newVal));
            }
        }
        if(!changes.isEmpty())
        {
            VConfigurationEvent e = new VConfigurationEvent(changes, getUnderlying().getName());
            Bukkit.getPluginManager().callEvent(e);
        }
    }
}
