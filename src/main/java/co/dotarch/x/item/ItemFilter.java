package co.dotarch.x.item;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * ItemFilter is a class representing a set of conditions enforced on an itemstack checked against the filter.
 * This is intended for mass-checking items' adherence to critieria.
 */
public class ItemFilter
{
    ArrayList<ItemCondition> conditions = new ArrayList<>();

    public boolean fits(ItemStack stack)
    {
        var pass = true;
        for (var condition : conditions)
        {
            pass = condition.fits(stack);
            if (!pass)
            {
                return pass;
            }
        }
        return pass;
    }

    public ItemFilter attach(ItemCondition condition)
    {
        conditions.add(condition);
        return this;
    }

    
}
