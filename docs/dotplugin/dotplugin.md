# Usage

## The DotX Plugin
DotX provides a **DotPlugin** class. This class lets you use DotX features with significantly reduced boilerplate code in your plugin class.

### ExamplePlugin.java

```java
package com.example;

import co.dotarch.x.DotPlugin;
import co.dotarch.x.data.DotPlayer;

public class DotExample extends DotPlugin
{

    private static DotExample theInstance; // You'll need this lol.

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

    /**
     * Used to magically get plugin instance when peforming VConfig on your config files.
     * @return Your plugin instance
     */
    public static DotExample instance()
    {
        return self;
    }

    /**
     * This allows you to use property initialization methods on DotPlayers whenever the player logs in
     * 
     * Use this to set your default values for new players, and do whatever else you need when someone logs in.
     * 
     * For more advanced logic, you are welcome to use
     * 
     * @param player The player who just logged into the server, and whose information has been loaded.
     */
    @Override
    public void handleLoadedPlayer(DotPlayer player)
    {
    }
}

```