package dotarch.api.commands;

import com.google.gson.Gson;
import dotarch.api.DotAPI;
import dotarch.api.data.PlayerCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static java.util.Map.entry;

public class DotAPICommand implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        var version = DotAPI.instance().getPluginMeta().getVersion();
        Component versionMsg = DotAPI.instance().messages().message("version-msg").apply(new String[]{version});
        Component title = DotAPI.instance().messages().message("title").apply(new String[]{"DotX API"});
        Component combined = title.appendNewline().append(versionMsg);
        sender.sendMessage(combined);
        return true;
    }
}