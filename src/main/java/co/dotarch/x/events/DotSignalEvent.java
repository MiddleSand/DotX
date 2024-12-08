package co.dotarch.x.events;

import co.dotarch.x.DotPlugin;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DotSignalEvent extends Event
{

    private static final HandlerList HANDLERS = new HandlerList();
    private final HashMap<String, Object> symbols;
    private final Set<DotPlugin> touches = new HashSet<>();


    /*
    - Check if the event was handled by a given plugin or feature pack without using setCancelled
    - Get an instance of anything that touched this signal, or at least the DotPlugin associated
     */

    @Getter
    private final String name;

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    public DotSignalEvent(String name, HashMap<String, Object> symbols)
    {
        this.name = name;
        this.symbols = symbols;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    /**
     * Checks symbol table for referenced ID
     *
     * @param id
     * @return Entry, or null if not present.
     */
    public Object getSymbol(String id)
    {
        return this.symbols.get(id);
    }

    public void markTouch(DotPlugin touching)
    {
        touches.add(touching);
    }

    public boolean getTouching(DotPlugin query)
    {
        return touches.contains(query);
    }
}