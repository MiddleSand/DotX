package co.dotarch.x.commands.admin;

import co.dotarch.x.data.DotObject;
import co.dotarch.x.plugin.DotX;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerDataCommand implements CommandExecutor, TabCompleter
{

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {

        if(args.length < 1)
        {
            Component send =
                DotX.instance().messages().message("title").apply(new String[]{"/playerdata"})
                    .appendNewline()
                    .append(
                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                    ).appendNewline()
                    .appendNewline()
                    .append(
                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                    ).appendNewline()
                    .append(
                            DotX.instance().messages().message("list-item").apply(new String[]{
                                    "/playerdata <get|set|delete>"
                            })
                    );
            sender.sendMessage(send);
            return true;
        }

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

        if(args.length < 2)
        {
            switch (action)
            {

                case EObjectCommandActions.GET:
                {
                    Component send =
                            DotX.instance().messages().message("title").apply(new String[]{"/playerdata get"})
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotX.instance().messages().message("list-item").apply(new String[]{
                                                    "/playerdata get <player>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.SET:
                {
                    Component send =
                            DotX.instance().messages().message("title").apply(new String[]{"/playerdata set"})
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotX.instance().messages().message("list-item").apply(new String[]{
                                                    "/playerdata set <player> <key> <newvalue>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.DELETE:
                {
                    Component send =
                            DotX.instance().messages().message("title").apply(new String[]{"/playerdata delete"})
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotX.instance().messages().message("list-item").apply(new String[]{
                                                    "/playerdata delete <player> <key>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }

                default:
                    // impossible
                    return false;
            }
        }

        if(args.length < 3)
        {
            switch (action)
            {

                case EObjectCommandActions.GET:
                {
                    String player = args[1];

                    var value = DotX.api().players().get(Objects.requireNonNull(Bukkit.getPlayer(player))).serialize();

                    Component send =
                            DotX.instance().messages().message("list-item").apply(new String[]{
                                    value
                            });
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.SET:
                {
                    Component send =
                            DotX.instance().messages().message("title").apply(new String[]{"/playerdata set"})
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotX.instance().messages().message("list-item").apply(new String[]{
                                                    "/playerdata set <player> <key> <newvalue>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.DELETE:
                {
                    Component send =
                            DotX.instance().messages().message("title").apply(new String[]{"/playerdata delete"})
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotX.instance().messages().message("list-item").apply(new String[]{
                                                    "/playerdata delete <player> <key>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }

                default:
                    // impossible
                    return false;
            }
        }

        if(args.length < 4)
        {
            switch (action)
            {
                case EObjectCommandActions.SET:
                {
                    Component send =
                            DotX.instance().messages().message("title").apply(new String[]{"/playerdata set"})
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                                    ).appendNewline()
                                    .appendNewline()
                                    .append(
                                            DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                                    ).appendNewline()
                                    .append(
                                            DotX.instance().messages().message("list-item").apply(new String[]{
                                                    "/playerdata set <player> <key> <newvalue>"
                                            })
                                    );
                    sender.sendMessage(send);
                    return true;
                }
                case EObjectCommandActions.DELETE:
                {
                    String player = args[1];
                    String key = args[2];

                    DotX.api().players().get(Objects.requireNonNull(Bukkit.getPlayer(player))).removeProperty(key, true);

                    Component send =
                            DotX.instance().messages().message("list-item").apply(new String[]{"Value deleted."});
                    sender.sendMessage(send);
                    return true;
                }

                default:
                    // impossible
                    return false;
            }
        }

        switch (action)
        {
            case EObjectCommandActions.SET:
            {
                String player = args[1];
                String key = args[2];
                String rawValue = String.join(" ", Arrays.stream(args, 4, args.length).toArray(String[]::new));

                DotX.api().players().get(Bukkit.getPlayer(player)).writeProperty(key, rawValue, true);

                Component send =
                        DotX.instance().messages().message("list-item").apply(new String[]{"Player data updated."});
                sender.sendMessage(send);
                return true;
            }

            default:Component send =
                    DotX.instance().messages().message("title").apply(new String[]{"/playerdata"})
                            .appendNewline()
                            .append(
                                    DotX.instance().messages().message("playerdata-welcome").apply(new String[]{})
                            ).appendNewline()
                            .appendNewline()
                            .append(
                                    DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                            ).appendNewline()
                            .append(
                                    DotX.instance().messages().message("list-item").apply(new String[]{
                                            "/playerdata <get|set|delete>"
                                    })
                            );
                sender.sendMessage(send);
                return true;
        }

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