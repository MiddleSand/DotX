package dotarch.api.commands;

import dotarch.api.DotAPI;
import dotarch.api.DotPlugin;
import dotarch.api.config.VirtualConfigEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VConfigCommand implements CommandExecutor
{

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if(args.length < 1)
        {
            sendHelpMessage(sender);
            return true;
        }
        switch(args[0])
        {
            default:
            {
                sendHelpMessage(sender);
            }
            case "update": {
                if(args.length < 5)
                {
                    Component send =
                            DotAPI.instance().messages().message("title").apply(new String[]{"/vconfig update"})
                                    .appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("vconfig-update-help").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("list-item").apply(new String[]{
                                                    "/vconfig update <plugin class> <filepath> <config key> <new value>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }
                Class targetPluginClass = null;
                // Check class exists
                try
                {
                    targetPluginClass = Class.forName(args[1]);
                } catch (ClassNotFoundException e)
                {
                    Component send =
                            DotAPI.instance().messages().message("title").apply(new String[]{"Command error"})
                                    .appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("list-item").apply(new String[]{
                                                    "Plugin class '" + args[1] + "' doesn't seem to exist! Cannot hot-edit config..."
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }
                // Check class is DotPlugin
                if(!DotPlugin.class.isAssignableFrom(targetPluginClass))
                {
                    Component send =
                            DotAPI.instance().messages().message("title").apply(new String[]{"Command error"})
                                    .appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("list-item").apply(new String[]{
                                                    "Plugin class '" + args[1] + "' isn't a DotX-enabled plugin! Cannot hot-edit config..."
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }

                // Try to get the plugin
                DotPlugin configurablePlugin = null;
                try
                {
                    configurablePlugin = (DotPlugin) (targetPluginClass.getMethod("instance").invoke(null));
                } catch (NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e)
                {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }

                // Check if config file actually exists
                if(!Arrays.stream(configurablePlugin.getConfigFilenames()).toList().contains(args[2]))
                {
                    Component send =
                            DotAPI.instance().messages().message("title").apply(new String[]{"Command error"})
                                    .appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("list-item").apply(new String[]{
                                                    "Config file '" + args[2] + "' does not exist in plugin. Cannot hot-edit config..."
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }

                // try and add the value
                try
                {
                    var entry = configurablePlugin.getVConfig(args[2]).getEntry(args[3]);
                    entry.setValue(VirtualConfigEntry.parsePrimitive(args[4]));
                }
                catch (Exception e)
                {
                    Component send =
                            DotAPI.instance().messages().message("title").apply(new String[]{"Command error"})
                                    .appendNewline()
                                    .append(
                                            DotAPI.instance().messages().message("list-item").apply(new String[]{
                                                    "Error editing value, incident details have been logged. Cannot hot-edit config..."
                                            })
                                    );
                    sender.sendMessage(send);
                    DotAPI.instance().getLogger().severe(Arrays.toString(e.getStackTrace()));
                    return true;
                }
                Component send =
                        DotAPI.instance().messages().message("title").apply(new String[]{"Modified"});
                sender.sendMessage(send);
                return true;
            }
        }
    }

    public void sendHelpMessage(final CommandSender sendTo)
    {
        Component send =
                DotAPI.instance().messages().message("title").apply(new String[]{"VConfig"})
                        .appendNewline()
                        .append(
                                DotAPI.instance().messages().message("vconfig-welcome").apply(new String[]{})
                        ).appendNewline()
                        .appendNewline()
                        .append(
                                DotAPI.instance().messages().message("title").apply(new String[]{"Subcommands"})
                        ).appendNewline()
                        .append(
                                DotAPI.instance().messages().message("list-item").apply(new String[]{"/vconfig update"})
                        );
        sendTo.sendMessage(send);
    }
}