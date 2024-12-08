package co.dotarch.x.data;

import co.dotarch.x.plugin.DotX;
import com.saicone.rtag.RtagBlock;
import com.saicone.rtag.RtagItem;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BlockCache implements Listener
{
    private HashMap<Chunk, ArrayList<DotBlock>> chunkIndex = new HashMap<>();
    private HashMap<Block, DotBlock> blockIndex = new HashMap<>();


    public DotBlock get(Block block)
    {
        return blockIndex.get(block);
    }

    private void cacheChunk(Chunk chunk)
    {
        var list = DotX.databaseHelper.tryReadChunk(chunk, this);

        chunkIndex.put(chunk, list);
        for(var item : list)
        {
            blockIndex.put(item.getBlock(), item);
        }
    }

    private void decacheChunk(Chunk chunk)
    {
        var blocks = chunkIndex.remove(chunk);
        blocks.forEach((block) -> {
            block.save();
            blockIndex.remove(block.getBlock());
        });
    }

    public void saveAll()
    {
        blockIndex.forEach((key, dotBlock) -> {
            dotBlock.save();
        });
    }

    @EventHandler
    public void onChunkLoaded(ChunkLoadEvent e)
    {
        Chunk chunk = e.getChunk();
        this.cacheChunk(chunk);
    }

    @EventHandler
    public void onChunkUnloaded(ChunkUnloadEvent e)
    {
        Chunk chunk = e.getChunk();
        this.decacheChunk(chunk);
    }

    // == Block events passed to DotBlock behavior classes ==


    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        RtagItem item = new RtagItem(event.getItemInHand());
        if(
                item.hasTag(DotBlock.BLOCK_CLASS_NBT_PROPERTY)
        )
        {
            String className = item.get(DotBlock.BLOCK_CLASS_NBT_PROPERTY);
            HashMap<String, String> injectProperties = new HashMap<>();
            // Lets the DotBlock resolve its own behavior on setup
            injectProperties.put(DotBlock.BLOCK_BEHAVIOR_PROPERTY, className);
            DotBlock newBlock = new DotBlock(
                    this,
                    event.getBlock(),
                    UUID.randomUUID(),
                    injectProperties
                    );
            DotX.databaseHelper.createBlock(newBlock);
            blockIndex.put(event.getBlock(),newBlock);
            chunkIndex.get(event.getBlock().getChunk()).add(newBlock);

            if(newBlock.getBehaviorClass() != null)
            {
                try
                {
                    newBlock.getBehaviorClass().getMethod("onPlace", DotBlock.class, BlockPlaceEvent.class).invoke(null, newBlock, event);
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        var dotBlock = blockIndex.get(event.getBlock());
        if(dotBlock != null && dotBlock.getBehaviorClass() != null)
        {
            try
            {
                dotBlock.getBehaviorClass().getMethod("onBreak", DotBlock.class, BlockBreakEvent.class).invoke(null, dotBlock, event);
                if((Boolean) dotBlock.getBehaviorClass().getMethod("shouldDrop", DotBlock.class).invoke(null, dotBlock))
                {
                    event.setDropItems(false);
                    event.getBlock().getDrops(event.getPlayer().getActiveItem()).forEach(itemStack ->
                        {
                            RtagBlock blockTags = new RtagBlock(event.getBlock());
                            RtagItem item = new RtagItem(itemStack);
                            item.set(blockTags.get(DotBlock.BLOCK_CLASS_NBT_PROPERTY), DotBlock.BLOCK_CLASS_NBT_PROPERTY);
                            item.update();
                            event.getPlayer().getInventory().addItem(itemStack);
                        }
                    );
                }
                // TODO: check if blocks should drop items with the correct behavior tag and nbt data haha
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
