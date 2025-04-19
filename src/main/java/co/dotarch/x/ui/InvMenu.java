package co.dotarch.x.ui;

import co.dotarch.x.data.DotPlayer;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class InvMenu
{
    private HashMap<DotPlayer, Gui> menuMap;

    /**
     * Lands the player in this menu, setting up whatever is needed to display this menu.
     * @param player The DotPlayer to show this menu to.
     */
    public abstract void land(DotPlayer player);

    /**
     * Called when a player leaves the menu
     * @param player
     */
    public abstract void back(DotPlayer player);

    /**
     * Check if a menu can accept a player before joining it.
     * @param player Player to check
     * @return True if this player should be allowed to see this menu, false otherwise.
     */
    public boolean canAccept(DotPlayer player)
    {
        return true;
    }

    public ArrayList<Pane> definedForEach(Function<Object, Pane> call, ArrayList<Object> items)
    {
        return new ArrayList<>(items.stream()
                .map(call)
                .toList());
    }


}
