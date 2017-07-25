package net.kineticraft.lostcity.discord;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.mechanics.metadata.MetadataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents a player sending a command from discord.
 * Created by Kneesnap on 6/28/2017.
 */
@Getter
public class DiscordSender implements CommandSender, MetadataHolder {

    private User user;
    private Message message;
    private List<String> messageQueue = new ArrayList<>();
    private BukkitTask sendTask;

    public DiscordSender(User user, Message message) {
        this.user = user;
        this.message = message;
    }

    /**
     * Get the channel the message was sent in.
     * @return channel
     */
    public MessageChannel getChannel() {
        return getMessage().getChannel();
    }

    @Override
    public void sendMessage(String s) {
        getMessageQueue().add(s);

        // Send the queued messages.
        if (getSendTask() == null) {
            this.sendTask = Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                DiscordAPI.getBot().sendMessage(getChannel(), String.join("\n", getMessageQueue()));
                getMessageQueue().clear();
                this.sendTask = null;
            });
        }
    }

    /**
     * Delete a message and send the user a PM explaining why something was deleted.
     * @param message
     */
    public void fail(String message) {
        getMessage().delete().queue();
        DiscordAPI.sendPrivate(getUser(), message);
    }

    @Override
    public void sendMessage(String[] strings) {
        sendMessage(String.join("\n", strings));
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return getMember().getEffectiveName();
    }

    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean b) {

    }

    /**
     * Get the member who sent the command.
     * @return member
     */
    public Member getMember() {
        return DiscordAPI.getServer().getMember(getUser());
    }

    @Override
    public boolean isPermissionSet(String s) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }
}
