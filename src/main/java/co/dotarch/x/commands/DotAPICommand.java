package co.dotarch.x.commands;

import co.dotarch.x.plugin.DotX;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DotAPICommand implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        var version = DotX.instance().getPluginMeta().getVersion();
        Component versionMsg = DotX.instance().messages().message("version-msg").apply(new String[]{version});
        Component title = DotX.instance().messages().message("title").apply(new String[]{"DotX API"});
        Component combined = title.appendNewline().append(versionMsg);
        sender.sendMessage(combined);
        return true;
    }
}