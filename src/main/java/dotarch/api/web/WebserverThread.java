package dotarch.api.web;

import dotarch.api.DotPlugin;
import dotarch.api.config.VirtualConfigEntry;
import dotarch.api.events.VConfigurationEvent;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class WebserverThread implements Listener
{
    @Getter
    private final DotPlugin plugin;

    @Getter
    private Javalin app;

    @Getter
    private final VirtualConfigEntry serverPort;

    public WebserverThread(DotPlugin plugin)
    {
        this.plugin = plugin;
        this.serverPort = plugin.getVConfig("config.yml").getEntry("web.port");
        this.plugin.getLogger().info("Starting DotX webserver at localhost:" + serverPort.getInt());
        this.app = Javalin.create(config -> {
                    config.staticFiles.add("/public", Location.CLASSPATH);
                })
                .get("/", ctx -> ctx.result("Hello World"))
                .start(serverPort.getInt());
        this.plugin.getLogger().info("Webserver started successfully!");
    }

    @EventHandler
    public void onConfigChange(VConfigurationEvent e)
    {
        if(
                Objects.equals(e.getFilename(), plugin.getConfigFilenameForClass(this.getClass()))
                        && e.getSymbol(serverPort.getKey()) != null
                        && e.getOwningPlugin() == this.plugin
        )
        {
            app.stop();
            this.app = Javalin.create(config -> {
                        config.staticFiles.add("/public", Location.CLASSPATH);
                    })
                    .get("/", ctx -> ctx.result("Hello World"))
                    .start(serverPort.getInt());
        }
    }

}
