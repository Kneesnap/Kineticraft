package net.kineticraft.lostcity.discord;

import lombok.Getter;
import net.dv8tion.jda.client.events.group.GroupUserJoinEvent;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.CommandType;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

/**
 * Basic Discord bot.
 * Handles discord events.
 * Created by Kneesnap on 6/28/2017.
 */
@Getter
public class DiscordBot extends ListenerAdapter {

    private JDA bot;
    private MessageChannel lastChannel;
    private boolean active;

    public DiscordBot() {
        setup();
    }

    /**
     * Setup this bot.
     */
    private void setup() {
        if (Configs.getMainConfig().getDiscordToken() == null) {
            Core.warn("Discord Token is not set, discord integrations will not function.");
            return;
        }

        try {
            bot = new JDABuilder(AccountType.BOT).setToken(Configs.getMainConfig().getDiscordToken())
                    .addEventListener(this).setAutoReconnect(true).setGame(Game.of("Kineticraft"))
                    .setStatus(OnlineStatus.ONLINE).buildAsync(); // Setup listener and connect to discord.
            active = true;
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to setup discord bot.");
        }
    }

    /**
     * Send a message to the given channel.
     * @param channel
     * @param message
     */
    public void sendMessage(DiscordChannel channel, String message) {
        sendMessage(channel.getChannel(), message, null);
    }

    /**
     * Send a message to the given channel.
     * @param channel
     * @param message
     * @param callback
     */
    public void sendMessage(DiscordChannel channel, String message, Consumer<Message> callback) {
        sendMessage(channel.getChannel(), message, callback);
    }

    /**
     * Send a message to the given channel.
     * @param channel
     * @param message
     */
    public void sendMessage(MessageChannel channel, String message) {
        sendMessage(channel, message, null);
    }

    /**
     * Send a message to the given channel.
     * @param channel
     * @param original
     * @param callback
     */
    public void sendMessage(MessageChannel channel, String original, Consumer<Message> callback) {
        if (channel == null || original == null)
            return;

        if (!DiscordAPI.isAlive()) {
            // We're not connected, queue the message.
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> sendMessage(channel, original, callback), 20L);
            return;
        }


        String message = ChatColor.stripColor(original); // Allows in-game messages to get sent both there and to discord without change.
        for (Role role : DiscordAPI.getServer().getRoles())
            message = message.replaceAll("@" + role.getName(), role.getAsMention());

        channel.sendMessage(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message))).queue(callback);
    }

    /**
     * Reply to the last message received.
     * @param message
     */
    public void reply(String message) {
        sendMessage(getLastChannel(), message);
    }

    @Override
    public void onReady(ReadyEvent evt) {
        DiscordAPI.sendGame("Server has completed startup.");
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent evt) {
        if(DiscordChannel.getChannel(evt.getChannel()) == DiscordChannel.ORYX && !evt.getUser().isBot())
            CommandServerVote.scanChannel();
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent evt) {
        if(DiscordChannel.getChannel(evt.getChannel()) == DiscordChannel.ORYX && !evt.getUser().isBot())
            CommandServerVote.scanChannel();
    }

    @Override // If a user leaves discord and re-joins, give them their rank and such.
    public void onGroupUserJoin(GroupUserJoinEvent evt) {
        KCPlayer kc = KCPlayer.getDiscord(evt.getUser());
        if (kc != null)
            kc.updateDiscord();
    }

    @Override
    public void onUserNameUpdate(UserNameUpdateEvent event) {
        fixName(event.getUser());
    }

    /**
     * Update the user's discord name to match their in-game name.
     * @param user
     */
    private static void fixName(User user) {
        if (!DiscordAPI.isVerified(user))
            return;

        KCPlayer player = KCPlayer.getDiscord(user);
        if (!DiscordAPI.getMember(user).getEffectiveName().equals(player.getUsername()))
            DiscordAPI.setNick(user, player.getUsername());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return; // We don't listen for other bot messages, period.

        lastChannel = event.getChannel();

        // Reply to PMs
        if (event.getChannelType() == ChannelType.PRIVATE) {
            reply("I'm sorry, I can only reply to messages on the Kineticraft discord.");
            return;
        }

        if (event.getChannelType() != ChannelType.TEXT)
            return; // We don't listen for this channel or user.

        DiscordChannel channel = DiscordChannel.getChannel(getLastChannel());
        DiscordSender sender = new DiscordSender(event.getAuthor(), event.getMessage());
        String noColor = ChatColor.stripColor(event.getMessage().getContent());
        final String message = noColor.substring(0, Math.min(128, noColor.length())); // Limit size of message.

        if (DiscordAPI.isVerified(event.getAuthor())) {
            KCPlayer p = KCPlayer.getWrapper(sender);

            if (p == null) { // If they have the tag
                DiscordAPI.setRoles(sender.getUser()); // Remove roles.
                reply("No player is associated with your discord account, please re-verify.");
                return;
            }


            if (p.isMuted()) {
                sender.fail("You are muted. Please wait " + p.getMute().untilExpiry() + " before talking.");
                return;
            }
        }

        if (channel == DiscordChannel.INGAME) {
            // Mirror the message into in-game.
            // Also will attempt to run the input as a slash command.
            // For some reason the sender is cast to CommandBlockSender throwing an async exception, so we have to do it sync.

            KCPlayer p = KCPlayer.getDiscord(event.getAuthor());
            if (p.isBanned()) {
                sender.fail("You may not use in-game chat until your ban expires.");
                return;
            }

            if (!Commands.handleCommand(sender, CommandType.SLASH, message) && !CommandType.DISCORD.matches(message)) {
                if (message.length() > 0)
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> Bukkit.broadcastMessage(
                            ChatColor.GRAY.toString() + ChatColor.BOLD + "DISCORD" + ChatColor.GRAY + " "
                                    + Utils.getSenderName(sender) + ChatColor.GRAY + ": " + ChatColor.WHITE + message));
                return;
            }
        }

        // Handle as a discord command.
        Commands.handleCommand(sender, CommandType.DISCORD, message);
    }
}