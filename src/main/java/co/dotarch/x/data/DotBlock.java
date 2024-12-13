package co.dotarch.x.data;

import co.dotarch.x.block.BlockBehavior;
import co.dotarch.x.plugin.DotX;
import com.saicone.rtag.RtagBlock;
import lombok.Getter;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.UUID;

public class DotBlock
{

    public static final String BLOCK_BEHAVIOR_PROPERTY = "class";
    public static final String BLOCK_CLASS_NBT_PROPERTY = "dotx_class";

    private final BlockCache cache;


    public BlockCache cache()
    {
        return cache;
    }

    @Getter
    private final Block block;

    @Getter
    private final UUID uuid;

    @Getter
    private Class<? extends BlockBehavior> behaviorClass = null;

    /**
     * Special properties:
     * - class: The behaviorclass of the block
     */
    private final HashMap<String, String> properties;


    public DotBlock(BlockCache cache, Block block, UUID uuid, HashMap<String, String> properties)
    {
        this.cache = cache;
        this.block = block;
        this.uuid = uuid;
        this.properties = properties;

        if(properties.containsKey(BLOCK_BEHAVIOR_PROPERTY))
        {
            String className = properties.get(BLOCK_BEHAVIOR_PROPERTY); // reference path to class
            RtagBlock tag = new RtagBlock(block);
            tag.set(properties.get(BLOCK_BEHAVIOR_PROPERTY), "dotx_class");
            try
            {
                behaviorClass = (Class<? extends BlockBehavior>) Class.forName(className);
            }
            catch (ClassNotFoundException e)
            {
                DotX.api().getLogger().warning("DotBlock '" + this.getUuid() + "' at location '" + this.getBlock().getLocation() + "' has invalid behavior class '" + className + "': The class does not exist...");
            }
            catch (Exception e)
            {
                DotX.api().getLogger().warning("DotBlock '" + this.getUuid() + "' at location '" + this.getBlock().getLocation() + "' has invalid behavior class '" + className + "': An unknown error occurred, did the class extend BehaviorClass?");
            }
        }
        else
        {
            behaviorClass = null;
        }

    }

    static final HashMap<String, String> initHashMap = new HashMap<String, String>();
    private final HashMap<String, String> transientProperties = new HashMap<String, String>();

    String serialize()
    {
        return DotX.instance().gson().toJson(properties);
    }

    public void save()
    {
        DotX.databaseHelper.updateBlock(this);
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
            DotX.api().getLogger().warning("Tracked property '" + key + "' of DotBlock '" + this.getUuid() + "' at location '" + this.getBlock().getLocation() + "' is null upon read...");
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
            DotX.api().getLogger().warning("Transient property '" + key + "' of DotBlock '" + this.getUuid() + "' at location '" + this.getBlock().getLocation() + "' is null upon read...");
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
