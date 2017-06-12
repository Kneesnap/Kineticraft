package net.kineticraft.lostcity.commands;

import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
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
    private boolean playerOnly;

    public PlayerCommand(String usage, String help, String... alias) {
        this(EnumRank.MU, true, usage, help, alias);
    }

    public PlayerCommand(EnumRank minRank, boolean playerOnly, String usage, String help, String... alias) {
        this(minRank, minRank.isAtLeast(EnumRank.MEDIA) ? CommandType.SLASH : CommandType.CHAT, playerOnly, usage, help, alias);
    }

    public PlayerCommand(EnumRank minRank, CommandType type, boolean playerOnly, String usage, String help, String... alias) {
        super(type, usage, help, alias);
        this.playerOnly = playerOnly;
        this.minRank = minRank;
    }

    @Override
    public void handle(CommandSender sender,  String label, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (isPlayerOnly() && !isPlayer) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return;
        }

        if (isPlayer && !KCPlayer.getWrapper((Player) sender).isRank(getMinRank()))
            return;

        super.handle(sender, label, args);
    }
}
