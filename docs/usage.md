# Usage

## The DotX Plugin
DotX provides a **DotPlugin** class. This class lets you use the full featureset of DotX with significantly reduced boilerplate code.

### ExamplePlugin.java

```java
package dotarch.api;

import co.dotarch.x.DotPlugin;
import co.dotarch.x.data.DotPlayer;

public class DotX extends DotPlugin
{

    private static DotX theInstance; // You'll need this lol.


    @Override
    public void onEnable()
    {
        // This allows DotX to register your plugin and read your configurations automagically.
        super.onEnable();
        // Reference variable, important for command-based configuration and accessing the plugin from elsewhere.
        theInstance = this;
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public void fullReload()
    {
        // Anything special that needs to happen if the entire plugin needs to be reloaded. This should be avoided as it is called rarely.
    }

    @Override
    public String getConfigFilenameForClass(Class clazz)
    {
        // Use this to route to config files based on the calling class.
        return "config.yml";
    }

    @Override
    public String[] getConfigFilenames()
    {
        // Your config files, aside from messages.yml, must be in this array, otherwise DotX cannot virtualize them.
        return new String[]
                {
                        "config.yml"
                };
    }

    public static DotX instance()
    {
        // Use to get your plugin, and called by the DotX command /vconfig update to get your plugin instance.
        return self;
    }

    @Override
    public void handleLoadedPlayer(DotPlayer player)
    {
        // This is where you initialize your properties, including transient ones.
    }
}

```