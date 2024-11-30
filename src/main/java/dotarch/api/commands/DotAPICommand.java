package dotarch.api.commands;

import com.google.gson.Gson;
import dotarch.api.DotAPI;
import dotarch.api.data.PlayerCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static java.util.Map.entry;

public class DotAPICommand implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        var version = DotAPI.getInstance().getPluginMeta().getVersion();
        var comp = DotAPI.getInstance().messages().message("version-msg").apply(new String[]{version});
        sender.sendMessage(comp);
        return true;
    }
}