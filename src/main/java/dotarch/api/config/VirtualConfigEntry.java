package dotarch.api.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;

@AllArgsConstructor
@Getter
public class VirtualConfigEntry
{
    private final String key;
    @Setter
    private Object value;


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
}
