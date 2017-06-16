package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Handles chat formatting.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class Chat extends Mechanic {

    @EventHandler(priority = EventPriority.LOW) // Filter should happen after commands.
    public void onChat(AsyncPlayerChatEvent evt) {
        evt.setMessage(filterMessage(evt.getMessage()));
        evt.setFormat(KCPlayer.getWrapper(evt.getPlayer()).getDisplayPrefix() + " %s: " + ChatColor.WHITE + "%s");

        evt.getRecipients().stream().filter(p -> KCPlayer.getWrapper(p).getIgnored().containsIgnoreCase(evt.getPlayer()
                .getName())).forEach(evt.getRecipients()::remove);
    }

    /**
     * Change any keywords defined in the config to another keyword.
     * @param message
     * @return
     */
    public static String filterMessage(String message) {
        for (String replace : Configs.getMainConfig().getFilter().keySet())
            message = message.replaceAll(replace, Configs.getMainConfig().getFilter().get(replace));
        return message;
    }
}
