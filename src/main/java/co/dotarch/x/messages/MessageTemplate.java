package co.dotarch.x.messages;

import co.dotarch.x.plugin.DotX;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

public class MessageTemplate
{
    @Getter
    private String message;

    public MessageTemplate(String message)
    {
        this.message = message;
    }

    /**
     * Applies the list of arguments to this message. Doesn't support PAPI since there's no player.
     * @param arguments
     * @return Compiled MiniMessage component
     */
    public Component apply(String[] arguments)
    {
        // Build placeholders

        String compiledMessage = message;
        for (int i = 0; i < arguments.length; i++)
        {
            compiledMessage = compiledMessage.replace("{" + i + "}", arguments[i]);
        }
        return DotX.instance().miniMessage().deserialize(compiledMessage);
    }

    /**
     * Apply with per-player PAPI support.
     * @param arguments List of template args to submit to the template.
     * @param player The player against whom PAPI may be applied.
     * @return Compiled MiniMessage component
     */
    public Component apply(String[] arguments, Player player)
    {
        // Build placeholders

        String compiledMessage = message;
        for (int i = 0; i < arguments.length; i++)
        {
            compiledMessage = compiledMessage.replace("{" + i + "}", arguments[i]);
        }
        // Papi
        compiledMessage = PlaceholderAPI.setPlaceholders(player, compiledMessage);

        //Send MiniMessage
        return DotX.instance().miniMessage().deserialize(compiledMessage);
    }
}
