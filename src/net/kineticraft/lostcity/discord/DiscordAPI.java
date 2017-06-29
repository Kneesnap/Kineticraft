package net.kineticraft.lostcity.discord;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.managers.GuildController;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.mechanics.Mechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Control the discord bot.
 *
 * Created by Kneesnap on 6/28/2017.
 */
public class DiscordAPI extends Mechanic {

    @Getter private static DiscordBot bot;

    @Override
    public void onEnable() {
        bot = new DiscordBot();
    }

    @Override
    public void onDisable() {
        sendGame("Server shutting down...");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Announce chat to discord.
    public void onChat(AsyncPlayerChatEvent evt) {
        sendGame(String.format(evt.getFormat(), "**" + evt.getPlayer().getName() + "**", evt.getMessage()));
    }

    /**
     * Send a message to in-game discord.
     * @param message
     */
    public static void sendGame(String message) {
        sendMessage(DiscordChannel.INGAME, message);
    }

    /**
     * Broadcast a message into a discord channel.
     * @param channel
     * @param message
     */
    public static void sendMessage(DiscordChannel channel, String message) {
        if (isAlive())
            getBot().sendMessage(channel, message);
    }

    /**
     * Get the server this applies to.
     * @return server
     */
    public static Guild getServer() {
        return getBot().getBot().getGuildById(Configs.getMainConfig().getServerId());
    }

    /**
     * Get the server manager API.
     * @return manager
     */
    public static GuildController getManager() {
        return getServer().getController();
    }

    /**
     * Return whether or not the bot exists and is connected.
     * @return alive
     */
    public static boolean isAlive() {
        return getBot() != null;
    }

    /**
     * Get a role by the given name.
     * @param rollName
     * @return roll
     */
    public static Role getRole(String rollName) {
        return DiscordAPI.getServer().getRolesByName(rollName, true).get(0);
    }

    /**
     * Return the bot's user.
     * @return selfUser
     */
    public static SelfUser getUser() {
        return getBot().getBot().getSelfUser();
    }

    /**
     * Get the bot's member.
     * @return member.
     */
    public static Member getMember() {
        return getServer().getMember(getUser());
    }
}
