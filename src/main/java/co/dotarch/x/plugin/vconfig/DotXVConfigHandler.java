package co.dotarch.x.plugin.vconfig;

import co.dotarch.x.plugin.DotX;
import co.dotarch.x.events.VConfigurationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DotXVConfigHandler implements Listener
{
    @EventHandler
    public void onVConfig(VConfigurationEvent e)
    {
        if (e.getOwningPlugin() == DotX.instance())
        {
            if(e.symbolPresent(DotX.instance().getVConfig("config.yml").getEntry("web.port")))
            {
                DotX.instance().restartWebserver();
            }
        }
    }
}
