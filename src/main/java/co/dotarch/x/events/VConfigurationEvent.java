package co.dotarch.x.events;

import co.dotarch.x.DotPlugin;
import co.dotarch.x.config.VirtualConfigEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

@AllArgsConstructor
public class VConfigurationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final HashMap<String, Pair<Object, Object>> symbols;


    @Getter
    private final String filename;

    @Getter
    private final DotPlugin owningPlugin;

    public static HandlerList getHandlerList() {
        return HANDLERS;
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

    public boolean symbolPresent(VirtualConfigEntry e)
    {
        return symbols.containsKey(e.getKey());
    }
}