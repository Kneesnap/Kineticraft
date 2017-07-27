package net.kineticraft.lostcity.commands;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Represents a commands that can be run in-game as a player.
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public abstract class PlayerCommand extends Command {

    private EnumRank minRank;
    @Setter private boolean dangerous;

    public PlayerCommand(String usage, String help, String... alias) {
        this(EnumRank.MU, usage, help, alias);
    }

    public PlayerCommand(EnumRank minRank, String usage, String help, String... alias) {
        this(minRank, CommandType.SLASH, usage, help, alias);
    }

    public PlayerCommand(EnumRank minRank, CommandType type, String usage, String help, String... alias) {
        super(type, usage, help, alias);
        this.minRank = minRank;
    }

    /**
     * Send a message formatted as a value to a CommandSender.
     * @param sender
     * @param name
     * @param value
     */
    protected void sendValue(CommandSender sender, String name, Object value) {
        if (value instanceof Boolean)
            value = Utils.formatToggle(null, (Boolean) value);
        sender.sendMessage(" - " + ChatColor.GRAY + name + ": " + ChatColor.WHITE + value);
    }

    @Override
    public boolean canUse(CommandSender sender, boolean showMessage) {
        boolean passRank = Utils.getRank(sender).isAtLeast(getMinRank());
        if (!passRank && showMessage)
            sender.sendMessage(ChatColor.RED + "You must be at least rank " + getMinRank().getName() + " to use this command.");

        boolean passDanger = !isDangerous() || Core.isDev(sender);
        if (!passDanger && showMessage)
            sender.sendMessage(ChatColor.RED + "This command is restricted!");

        return passRank && passDanger;
    }
}
