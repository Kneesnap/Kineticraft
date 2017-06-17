package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

/**
 * Handles chat formatting.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class Chat extends Mechanic {

    private static final Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");

    @EventHandler(priority = EventPriority.LOW) // Filter should happen after commands.
    public void onChat(AsyncPlayerChatEvent evt) {
        evt.setMessage(filterMessage(evt.getMessage()));
        evt.setFormat(KCPlayer.getWrapper(evt.getPlayer()).getDisplayPrefix() + " %s: " + ChatColor.WHITE + "%s");

        // Handle ignored players.
        evt.getRecipients().stream().filter(p -> KCPlayer.getWrapper(p).getIgnored().containsIgnoreCase(evt.getPlayer()
                .getName())).forEach(evt.getRecipients()::remove);

        // Don't allow URLs from Mu.
        if (Utils.getRank(evt.getPlayer()) == EnumRank.MU)
            evt.setMessage(filterURL(evt.getMessage()));

        // Color Omega+
        if (Utils.getRank(evt.getPlayer()).isAtLeast(EnumRank.OMEGA))
            evt.setMessage(ChatColor.translateAlternateColorCodes('&', evt.getMessage()));
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

    /**
     * Remove a URL from a message.
     * @param input
     * @return filtered
     */
    public static String filterURL(String input) {
        String text = URL_PATTERN.matcher(input).replaceAll("$1 $2");
        while (URL_PATTERN.matcher(text).find())
            text = URL_PATTERN.matcher(text).replaceAll("$1 $2");
        return text;
    }
}
