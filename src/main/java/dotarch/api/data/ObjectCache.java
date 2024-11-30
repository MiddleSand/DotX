package dotarch.api.data;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class ObjectCache
{
    private static HashMap<String, HashMap<UUID, DotObject>> cache = new HashMap<>();

    public DotObject get(UUID uuid, String type, HashMap<String, String> initializationMapIfMissing)
    {
        if(cache.containsKey(type) && cache.get(type).containsKey(uuid))
        {
            return cache.get(type).get(uuid);
        }
        else
        {
            if(initializationMapIfMissing == null)
            {
                return DotObject.load(uuid, type, this);
            }
            else
            {
                return DotObject.load(uuid, type, this, initializationMapIfMissing);
            }
        }
    }

    protected void addToCache(DotObject obj)
    {
        if(cache.containsKey(obj.type()))
        {
            var set = cache.get(obj.type());
            set.put(obj.uuid(), obj);
        }
        else
        {
            var set = new HashMap<UUID, DotObject>();
            set.put(obj.uuid(), obj);
            cache.put(obj.type(), set);
        }
    }

    private void removeFromCache(DotObject obj)
    {
        if(cache.containsKey(obj.type()) && cache.get(obj.type()).containsKey(obj.uuid()))
        {
            cache.get(obj.type()).remove(obj.uuid());
            // Cleanup type if not needed anymore
            if(cache.get(obj.type()).isEmpty())
            {
                cache.remove(obj.type());
            }
        }
        else
        {
            // This should never happen.
            throw new RuntimeException("Object " + obj.uuid().toString() + " (" + obj.type() + ", " + obj.serialize() + ") attempted cache removal and somehow wasn't in the cache to begin with. Something is very wrong!");
        }
    }

    public void saveAll()
    {
        cache.forEach((type, map) -> {
            map.forEach((uuid, obj) -> {
                obj.save();
            });
        });
    }

}
