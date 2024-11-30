package dotarch.api.events;

import dotarch.api.data.DotObject;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

public class DotSignalEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final HashMap<String, Object> symbols;

    @Getter
    private final String name;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public DotSignalEvent(String name, HashMap<String, Object> symbols)
    {
        this.name = name;
        this.symbols = symbols;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Checks symbol table for referenced ID
     * @param id
     * @return Entry, or null if not present.
     */
    public Object getSymbol(String id) { return this.symbols.get(id); }

}