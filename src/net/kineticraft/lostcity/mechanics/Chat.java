package net.kineticraft.lostcity.mechanics;

import lombok.Getter;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles chat formatting.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class Chat extends Mechanic {

    @Getter
    private static Map<String, String> replace = new HashMap<>();

    static {
        replace.put("shrug", "¯\\\\_(ツ)_/¯");
        replace.put("awesome", "ᕕ( ᐛ )ᕗ");
        replace.put("卐", "☹");
        replace.put("卍", "☹");
        replace.put("☭", "☹");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent evt) {
        evt.setMessage(filterMessage(evt.getMessage()));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        KCPlayer player = KCPlayer.getWrapper(evt.getPlayer());
        evt.setMessage(filterMessage(evt.getMessage()));
        evt.setFormat(player.getRank().getChatPrefix() + " %s: " + ChatColor.WHITE + "%s");
    }

    public static String filterMessage(String message) {
        for (String replace : getReplace().keySet())
            message = message.replaceAll(replace, getReplace().get(replace));
        return message;
    }
}
