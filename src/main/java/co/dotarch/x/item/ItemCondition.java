package co.dotarch.x.item;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class ItemCondition
{
    Method matches;
    String name;
    public boolean fits(ItemStack check)
    {
        try
        {
            return (boolean) matches.invoke(this, check);
        } catch (IllegalAccessException e)
        {
            throw new ItemFilteringException(
                    "Cannot check itemstack with condition " + name + " - the bound method is not accessible."
            );
        } catch (InvocationTargetException e)
        {
            e.printStackTrace(); // i really don't care
            throw new RuntimeException(
                    "Cannot check itemstack with condition " + name + " - the check threw an exception: " + e.getMessage()
            );
        }
    }

}
