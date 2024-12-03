package dotarch.api.plugin.vconfig;

import dotarch.api.DotX;
import dotarch.api.events.VConfigurationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DotAPIVConfig implements Listener
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
