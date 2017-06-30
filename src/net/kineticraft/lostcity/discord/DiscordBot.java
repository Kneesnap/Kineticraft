package net.kineticraft.lostcity.discord;

import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.CommandType;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.commands.DiscordSender;
import net.kineticraft.lostcity.commands.discord.CommandServerVote;
import net.kineticraft.lostcity.config.Configs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

/**
 * Basic Discord bot.
 * Handles discord events.
 *
 * Created by Kneesnap on 6/28/2017.
 */
@Getter
public class DiscordBot extends ListenerAdapter {

    private JDA bot;
    private MessageChannel lastChannel;

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
     * @param message
     * @param callback
     */
    public void sendMessage(MessageChannel channel, String message, Consumer<Message> callback) {
        if (channel == null || !DiscordAPI.isAlive())
            return;

        message = ChatColor.stripColor(message); // Allows in-game messages to get sent both there and to discord without change.
        for (Role role : DiscordAPI.getServer().getRoles())
            message = message.replaceAll("@" + role.getName(), role.getAsMention());

        channel.sendMessage(ChatColor.stripColor(message)).queue(callback);
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

        if (channel == DiscordChannel.INGAME) {
            // Mirror the message into in-game.
            // Also will attempt to run the input as a slash command.
            // For some reason the sender is cast to CommandBlockSender throwing an async exception, so we have to do it sync.

            if (!DiscordAPI.isVerified(event.getAuthor())) {
                reply("Please verify with /verify before using this feature.");
                return;
            }

            if (!Commands.handleCommand(sender, CommandType.SLASH, message) && !CommandType.DISCORD.matches(message)) {
                if (message.length() > 0)
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> Bukkit.broadcastMessage(
                            ChatColor.GRAY.toString() + ChatColor.BOLD + "DISCORD" + ChatColor.GRAY + " "
                                    + sender.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + message));
                return;
            }
        }

        // Handle as a discord command.
        Commands.handleCommand(sender, CommandType.DISCORD, message);
    }
}