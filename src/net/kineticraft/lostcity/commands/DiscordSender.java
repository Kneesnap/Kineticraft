package net.kineticraft.lostcity.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.mechanics.metadata.MetadataHolder;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * Represents a player sending a command from discord.
 *
 * Created by Kneesnap on 6/28/2017.
 */
@AllArgsConstructor @Getter
public class DiscordSender implements CommandSender, MetadataHolder {

    private User user;
    private MessageChannel channel;

    @Override
    public void sendMessage(String s) {
        DiscordAPI.getBot().sendMessage(getChannel(), s);
    }

    @Override
    public void sendMessage(String[] strings) {
        for (String s : strings)
            sendMessage(s);
    }

    @Override
    public Server getServer() {
        return null;
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
        throw new UnsupportedOperationException("Cannot set the op status of a discord user.");
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
