package dotarch.api.plugin.vconfig;

import dotarch.api.DotAPI;
import dotarch.api.events.VConfigurationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DotAPIVConfig implements Listener
{
    @EventHandler
    public void onVConfig(VConfigurationEvent e)
    {
        if (e.getOwningPlugin() == DotAPI.instance())
        {
            if(e.symbolPresent(DotAPI.instance().getVConfig("config.yml").getEntry("web.port")))
            {
                DotAPI.instance().restartWebserver();
            }
        }
    }
}
