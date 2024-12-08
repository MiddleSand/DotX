package co.dotarch.x.messages;

import org.bukkit.configuration.Configuration;

import java.util.HashMap;

/**
 * Quick class for making messages easier
 */
public class Messages
{
    HashMap<String, MessageTemplate> messages = new HashMap<String, MessageTemplate>();
    Configuration config;

    public Messages(Configuration config)
    {
        this.config = config;
        readTemplates();
    }

    /**
     * Reads all the message templates from the configured messages yml file.
     */
    public void readTemplates()
    {
        messages.clear();
        for (String key : config.getKeys(true)) {
            String rawMessage = (String) config.get(key);
            messages.put(key, new MessageTemplate(rawMessage));
        }
    }

    /**
     * Injects a new message template value into a given message key, persisting to configuration file.
     * Allows message hotswapping.
     *
     * @param key The key at which to inject
     * @param newTemplate The new template to use at this key
     */
    public void inject(String key, MessageTemplate newTemplate)
    {
        messages.put(key, newTemplate);
        config.set(key, newTemplate.getMessage());
    }

    /**
     * Retrives a defined message template from the registered list.
     * @param name Key at which the message is stored within the config file, e.g. messages.test.intro
     * @return The message template at that key, or NULL if none is found.
     */
    public MessageTemplate message(String name)
    {
        return messages.get(name);
    }

}
