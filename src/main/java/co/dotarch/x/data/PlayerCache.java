package co.dotarch.x.data;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerCache implements Listener
{
    private final HashMap<UUID, DotPlayer> cache = new HashMap<>();

    public DotPlayer get(Player player)
    {
        if(cache.containsKey(player.getUniqueId()))
        {
            return cache.get(player.getUniqueId());
        }
        else
        {
            return addToCache(player);
        }
    }

    private DotPlayer addToCache(Player p)
    {
        var dotPlayer = DotPlayer.load(p, this);
        cache.put(p.getUniqueId(), dotPlayer);
        return dotPlayer;
    }

    private void removeFromCache(Player p)
    {
        cache.remove(p.getUniqueId()).save();
    }

    public void saveAll()
    {
        cache.forEach((u, p) -> {
            p.save();
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        addToCache(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        removeFromCache(e.getPlayer());
    }

}
