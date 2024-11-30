package dotarch.api.events;

import dotarch.api.data.DotObject;
import dotarch.api.data.DotPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ObjectLoadedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final DotObject dotObject;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public ObjectLoadedEvent(DotObject dotObject)
    {
        this.dotObject = dotObject;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public DotObject getObject() { return this.dotObject; }

}