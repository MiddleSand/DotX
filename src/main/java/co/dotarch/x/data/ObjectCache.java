package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;

import java.util.HashMap;
import java.util.UUID;

public class ObjectCache
{
    private final HashMap<String, HashMap<String, DotObject>> cache = new HashMap<>();

    public DotObject get(String key, String type, HashMap<String, String> initializationMapIfMissing)
    {
        if(cache.containsKey(type) && cache.get(type).containsKey(key))
        {
            return cache.get(type).get(key);
        }
        else
        {
            if(initializationMapIfMissing == null)
            {
                return DotObject.load(key, type, this);
            }
            else
            {
                return DotObject.load(key, type, this, initializationMapIfMissing);
            }
        }
    }

    protected void addToCache(DotObject obj)
    {
        if(cache.containsKey(obj.type()))
        {
            var set = cache.get(obj.type());
            set.put(obj.key(), obj);
        }
        else
        {
            var set = new HashMap<String, DotObject>();
            set.put(obj.key(), obj);
            cache.put(obj.type(), set);
        }
    }

    private void removeFromCache(DotObject obj)
    {
        if(cache.containsKey(obj.type()) && cache.get(obj.type()).containsKey(obj.key()))
        {
            cache.get(obj.type()).remove(obj.key());
            // Cleanup type if not needed anymore
            if(cache.get(obj.type()).isEmpty())
            {
                cache.remove(obj.type());
            }
        }
        else
        {
            // This should never happen.
            throw new RuntimeException("Object " + obj.key() + " (" + obj.type() + ", " + obj.serialize() + ") attempted cache removal and somehow wasn't in the cache to begin with. Something is very wrong!");
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

    public boolean delete(String key, String type)
    {
        if(cache.containsKey(type))
        {
            var obj = cache.get(type).remove(key);
            if(obj != null)
            {
                obj.delete();
            }
            return obj != null;
        }
        return false;
    }
}
