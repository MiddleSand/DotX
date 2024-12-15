package co.dotarch.x.commands.admin;

import co.dotarch.x.block.BlockBehavior;
import co.dotarch.x.data.DotBlock;
import co.dotarch.x.plugin.DotX;
import com.saicone.rtag.RtagItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DotBlockCommand implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {

        if(args.length < 2)
        {
            Component send =
                    DotX.instance().messages().message("title").apply(new String[]{"/dotblock"})
                            .appendNewline()
                            .append(
                                    DotX.instance().messages().message("title").apply(new String[]{"Usage"})
                            ).appendNewline()
                            .append(
                                    DotX.instance().messages().message("list-item").apply(new String[]{
                                            "/dotblock <material> <behavior>"
                                    })
                            );
            sender.sendMessage(send);
        }
        else
        {
            // Validate material
            Material targetMaterial = Material.valueOf(args[0]);
            if(targetMaterial == null)
            {
                Component send =
                        DotX.instance().messages().message("title").apply(new String[]{"Command error"})
                                .appendNewline()
                                .append(
                                        DotX.instance().messages().message("list-item").apply(new String[]{
                                                "Material '" + args[0] + "' doesn't exist! Cannot give block..."
                                        })
                                );
                sender.sendMessage(send);
                return true;
            }
            // Ensure the block is actually a block
            if(!targetMaterial.isBlock())
            {
                Component send =
                        DotX.instance().messages().message("title").apply(new String[]{"Command error"})
                                .appendNewline()
                                .append(
                                        DotX.instance().messages().message("list-item").apply(new String[]{
                                                "Material '" + args[0] + "' isn't placeable as a block! Cannot give block..."
                                        })
                                );
                sender.sendMessage(send);
                return true;
            }

            // Validate behavior
            Class targetBehavior = null;
            try
            {
                targetBehavior = Class.forName(args[1]);
            } catch (ClassNotFoundException e)
            {
                Component send =
                        DotX.instance().messages().message("title").apply(new String[]{"Command error"})
                                .appendNewline()
                                .append(
                                        DotX.instance().messages().message("list-item").apply(new String[]{
                                                "Behavior class '" + args[1] + "' doesn't seem to exist! Cannot give block..."
                                        })
                                );
                sender.sendMessage(send);
                return true;
            }
            // Check class is BlockBehavior
            if(!BlockBehavior.class.isAssignableFrom(targetBehavior))
            {
                Component send =
                        DotX.instance().messages().message("title").apply(new String[]{"Command error"})
                                .appendNewline()
                                .append(
                                        DotX.instance().messages().message("list-item").apply(new String[]{
                                                "Behavior class '" + args[1] + "' doesn't extend BlockBehavior! Cannot give block..."
                                        })
                                );
                sender.sendMessage(send);
                return true;
            }

            // give item
            ItemStack stack = ItemStack.of(targetMaterial);
            RtagItem item = new RtagItem(stack);
            item.set(args[1], DotBlock.BLOCK_CLASS_NBT_PROPERTY);
            item.update();
            ((Player) sender).getInventory().addItem(stack);
        }

        return true;
    }
}