package co.dotarch.x.block;

import co.dotarch.x.data.DotBlock;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class BlockBehavior
{
    public static boolean shouldDrop(DotBlock block)
    {
        return true; // override if needed?!?!!?
    }

    public static void onInteract(DotBlock block, PlayerInteractEvent event)
    {
        // Do nothing - Overridden ?
    }

    public static void onPlace(DotBlock block, BlockPlaceEvent event)
    {
        // Do nothing - Overridden ?
    }

    public static void onBreak(DotBlock block, BlockBreakEvent event)
    {
        // Do nothing - Overridden ?
    }

    public static void onPlayerTouch(DotBlock block, PlayerMoveEvent event)
    {
        // Do nothing - Overridden ?
    }

    public static void onEntityTouch(DotBlock block, EntityMoveEvent event)
    {
        // Do nothing - Overridden ?
    }
}
