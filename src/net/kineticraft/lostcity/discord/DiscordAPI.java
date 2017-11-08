package net.kineticraft.lostcity.discord;

import lombok.Getter;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.managers.GuildController;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Control the discord bot.
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

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandDiscordVerify(), new CommandServerVote());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent evt) {
        sendGame(evt.getDeathMessage());
    }

    /**
     * Send a message to in-game discord.
     * @param message
     */
    public static void sendGame(String message) {
        sendMessage(DiscordChannel.INGAME, message.replace("_", "\\_"));
    }

    /**
     * Broadcast a message into a discord channel.
     * @param channel
     * @param message
     */
    public static void sendMessage(DiscordChannel channel, String message) {
        if (!isAlive()) {
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> sendMessage(channel, message), 10L);
            return;
        }
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
     * Return whether or not the bot exists and is connected, and enabled.
     * @return alive
     */
    public static boolean isAlive() {
        return getBot() != null && !ServerUtils.isDevServer() && Configs.getMainConfig().getServerId() != 0;
    }

    /**
     * Get a role by the given name.
     * @param rollName
     * @return roll
     */
    public static Role getRole(String rollName) {
        List<Role> roles = getServer().getRolesByName(rollName, true);
        return roles.isEmpty() ? null : roles.get(0);
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

    /**
     * Does this user have the given role on discord?
     * @param user
     * @param role
     * @return hasRole
     */
    public static boolean hasRole(User user, String role) {
        return isAlive() && getServer().getMember(user).getRoles().contains(getRole(role));
    }

    /**
     * Is this user verified on discord?
     * @param user
     * @return verified
     */
    public static boolean isVerified(User user) {
        return hasRole(user, "Verified") || hasRole(user, "Staff");
    }

    /**
     * Does our bot have permissions to edit this user?
     * @param user
     * @return perms
     */
    public static boolean canEdit(User user) {
        return isAlive() && getMember().canInteract(getServer().getMember(user));
    }

    /**
     * Send a private message to a discord user.
     * @param user
     * @param message
     */
    public static void sendPrivate(User user, String message) {
        user.openPrivateChannel().queue(c -> c.sendMessage(message).queue());
    }

    /**
     * Set the nickname of a discord user.
     * @param user
     * @param nick
     */
    public static void setNick(User user, String nick) {
        if (canEdit(user))
            getManager().setNickname(getServer().getMember(user), nick).queue();
    }

    /**
     * Set the roles of a given discord user.
     * @param user
     * @param roles
     */
    public static void setRoles(User user, String... roles) {
        if (!canEdit(user))
            return;

        Set<Role> roleSet = getMember(user).getRoleSet();
        roleSet.clear();
        roleSet.addAll(Arrays.stream(roles).map(DiscordAPI::getRole).filter(Objects::nonNull).collect(Collectors.toList()));
        getManager().addRolesToMember(getMember(user)).queue(); // Send the changes.
    }

    /**
     * Return the member for a given user.
     * @param user
     * @return member
     */
    public static MemberImpl getMember(User user) {
        return (MemberImpl) getServer().getMember(user);
    }
}
