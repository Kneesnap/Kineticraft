package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.TextUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Handles chat formatting.
 * Created by Kneesnap on 5/29/2017.
 */
public class Chat extends Mechanic {

    private static final Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[-\\w_.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
    private static final List<ChatColor> STAFF_ONLY = Arrays.asList(ChatColor.BLACK, ChatColor.MAGIC);
    private static final List<Function<String, String>> ZEN_FILTERS = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOW) // Filter should happen after commands.
    public void onChat(AsyncPlayerChatEvent evt) {

        // Handle mutes.
        KCPlayer pw = KCPlayer.getWrapper(evt.getPlayer());
        if (pw.isMuted() && !evt.isCancelled()) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage(ChatColor.RED + "You are muted for "  + pw.getMute().untilExpiry()+ ".");
            evt.getPlayer().sendMessage(ChatColor.RED + "Reason: " + ChatColor.YELLOW + pw.getMute().getReason());
            evt.getPlayer().sendMessage(ChatColor.RED + "Source: " + ChatColor.YELLOW + pw.getMute().getSource());
            return;
        }

        evt.setMessage(applyAllFilters(evt.getPlayer(), evt.getMessage())); // Apply all filters.
        evt.setFormat(KCPlayer.getWrapper(evt.getPlayer()).getDisplayPrefix() + " %s:" + ChatColor.WHITE + " %s");

        // Handle ignored players.
        if (!pw.getRank().isStaff())
            new ArrayList<>(evt.getRecipients()).stream()
                    .filter(p -> KCPlayer.getWrapper(p).getIgnored().containsIgnoreCase(evt.getPlayer().getName()))
                    .forEach(evt.getRecipients()::remove);
    }

    @EventHandler
    public void idiotMode(AsyncPlayerChatEvent evt) {
        if (System.currentTimeMillis() <= KCPlayer.getPlayer(evt.getPlayer()).getZenMode())
            evt.setMessage(Utils.randElement(ZEN_FILTERS).apply(evt.getMessage()));
    }

    /**
     * Censor a message.
     * @param s
     * @return censored
     */
    public static String censor(String s) {
        for (String badWord : Configs.getMainConfig().getSwearWords())
            s = s.replaceAll("(?i)" + badWord, TextUtils.makeString('*', badWord.length()));
        return s;
    }

    /**
     * Is a message obscene?
     * @param str
     * @return obscene
     */
    public static boolean isObscene(String str) {
        return !censor(str).equals(str);
    }

    /**
     * Apply all filters to this message, URL, message, and color.
     * @param sender
     * @param message
     * @return filtered
     */
    public static String applyAllFilters(CommandSender sender,  String message) {
        // Prevent Mus from advertising in chat.
        if (Utils.getRank(sender) == EnumRank.MU)
            message = filterURL(message);

        // Apply colors and emojis to this message
        return filterMessage(applyColor(sender, message));

    }

    /**
     * Change any keywords defined in the config to another keyword.
     * @param message
     * @return filtered
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

    /**
     * Apply color codes to a message based on the permissions of a player.
     *
     * @param sender
     * @param message
     * @return colored
     */
    public static String applyColor(CommandSender sender, String message) {
        EnumRank rank = Utils.getRank(sender);

        // Apply color
        if (rank.isAtLeast(EnumRank.OMEGA))
            message = ChatColor.translateAlternateColorCodes('&', message);

        // Remove illicit characters if not staff.
        if (!rank.isAtLeast(EnumRank.MEDIA))
            for (ChatColor c : STAFF_ONLY)
                message = message.replaceAll(ChatColor.COLOR_CHAR + String.valueOf(c.getChar()), "");

        return message;
    }

    static {
        final String[] WORDS = new String[] {"huehue", "snarff", "hurr", "durr", "hodor", "derp", "herp", "ROARK", "SNEE"};

        ZEN_FILTERS.addAll(Arrays.asList(s -> {
            // Scramble random words.
            String[] sp = s.split(" ");
            for (int i = 0; i < sp.length; i++)
                if (Utils.nextBool())
                    sp[i] = Utils.scramble(sp[i]);
            return String.join(" ", sp);
        }, s -> {
            // Replace random words with nonsense.
            String[] sp = s.split(" ");
            for (int i = 0; i < sp.length; i++)
                if (Utils.nextBool())
                    sp[i] = Utils.randElement(WORDS);
            return String.join(" ", sp);
        }, s -> Utils.randElement(Configs.getTextConfig(Configs.ConfigType.IDIOT).getLines())));
    }
}
