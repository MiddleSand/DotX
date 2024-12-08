package co.dotarch.x.events;

import co.dotarch.x.data.DotPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoadedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player mcPlayer;
    private final DotPlayer dotPlayer;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public PlayerLoadedEvent(Player mcPlayer, DotPlayer dotPlayer)
    {
        this.mcPlayer = mcPlayer;
        this.dotPlayer = dotPlayer;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getMCPlayer() {
        return this.mcPlayer;
    }

    public DotPlayer getDotPlayer() { return this.dotPlayer; }

}