package co.dotarch.x.config;

import co.dotarch.x.events.VConfigurationEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.regex.Pattern;

@AllArgsConstructor
@Getter
public class VirtualConfigEntry
{
    private final String key;

    private Object value;

    @SneakyThrows
    public void setValue(Object value)
    {
        this.value = value;
        var oldValue = this.parent.getUnderlying().get(this.key);
        this.parent.getUnderlying().set(key, value);
        this.parent.getUnderlying().save(this.parent.getPlugin().getDataFolder().getAbsolutePath() + "/" + this.parent.getFilename());
        var changes = new HashMap<String, Pair<Object, Object>>();
        changes.put(key, Pair.of(oldValue, this.value));

        VConfigurationEvent e = new VConfigurationEvent(changes, this.parent.getFilename(), this.getParent().getPlugin());
        Bukkit.getPluginManager().callEvent(e);
    }

    @Getter
    private VirtualizedConfiguration parent;


    public String getString()
    {
        return (String) value;
    }

    public int getInt()
    {
        return (int) value;
    }

    public double getDouble()
    {
        return (double) value;
    }

    public boolean getBoolean()
    {
        return (boolean) value;
    }

    public Array getArray()
    {
        return (Array) value;
    }

    public static Object parsePrimitive(String str) {
        if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(str);
        }

        if (Pattern.matches("\\d+\\.\\d+", str)) {
            return Double.parseDouble(str);
        }

        if (Pattern.matches("-?\\d{10,}", str)) {
            return Long.parseLong(str);
        }

        if (Pattern.matches("-?\\d{1,9}", str)) {
            return Integer.parseInt(str);
        }

        return str;
    }
}
