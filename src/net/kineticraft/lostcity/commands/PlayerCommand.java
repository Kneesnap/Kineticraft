package net.kineticraft.lostcity.commands;

import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a commands that can be run in-game as a player.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public abstract class PlayerCommand extends Command {

    private EnumRank minRank;

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

    @Override
    public boolean canUse(CommandSender sender, boolean showMessage) {
        boolean passRank = Utils.getRank(sender).isAtLeast(getMinRank());
        if (!passRank && showMessage)
            sender.sendMessage(ChatColor.RED + "You must be at least rank " + getMinRank().getName() + " to use this command.");

        return passRank;
    }
}
