package co.dotarch.x.commands.admin;

import co.dotarch.x.block.BlockBehavior;
import co.dotarch.x.data.DotBlock;
import co.dotarch.x.data.DotObject;
import co.dotarch.x.plugin.DotX;
import com.saicone.rtag.RtagItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ObjectCommand implements CommandExecutor, TabCompleter
{

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {

        if(args.length < 3)
        {
            Component send =
                    DotX.instance().messages().message("title").apply(new String[]{"/object"})
                            .appendNewline()
                            .append(
                                    DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                            ).appendNewline()
                            .append(
                                    DotX.instance().messages().message("list-item").apply(new String[]{
                                            "/object <get|set|delete> <type> <key> [value key] [new value]"
                                    })
                            );
            sender.sendMessage(send);
        }
        else
        {
            EObjectCommandActions action;
            // Validate operation
            try
            {
                action = EObjectCommandActions.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e)
            {
                Component send =
                        DotX.instance().messages().message("title").apply(new String[]{"Command error"})
                                .appendNewline()
                                .append(
                                        DotX.instance().messages().message("list-item").apply(new String[]{
                                                "Action '" + args[0] + "' doesn't exist, try 'set', 'get', or 'delete'..."
                                        })
                                );
                sender.sendMessage(send);
                return true;
            }

            switch (action)
            {

                case EObjectCommandActions.GET:
                {
                    String type = args[1];
                    String key = args[2];

                    Component send =
                            DotX.instance().messages().message("list-item").apply(new String[]{
                                    DotX.api().objects().get(key, type, DotObject.initHashMap).serialize()
                            });
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.SET:
                {
                    String type = args[1];
                    String key = args[2];
                    String valueKey = args[3];
                    String rawValue = String.join(" ", Arrays.stream(args, 4, args.length).toArray(String[]::new));

                    DotX.api().objects().get(key, type, DotObject.initHashMap).writeProperty(valueKey, rawValue, true);

                    Component send =
                            DotX.instance().messages().message("list-item").apply(new String[]{"Object data updated."});
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.DELETE:
                {
                    String type = args[1];
                    String key = args[2];

                    DotX.api().objects().delete(key, type);

                    Component send =
                            DotX.instance().messages().message("list-item").apply(new String[]{"Object deleted."});
                    sender.sendMessage(send);
                    return true;
                }

                default:
                    // impossible
                    return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(args.length == 0)
        {
            return List.of(
                    "get",
                    "set",
                    "delete"
            );
        }

        return List.of();
    }
}