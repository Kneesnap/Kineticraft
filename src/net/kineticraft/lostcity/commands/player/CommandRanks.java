package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.misc.CommandInfo;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Ranks - A list of all ranks.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandRanks extends CommandInfo {

    public CommandRanks() {
        super(Configs.ConfigType.RANKS, "List all ranks.", "ranks", "listranks");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Your rank: " + KCPlayer.getWrapper((Player) sender).getRank().getFullName());
        sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/rankup" + ChatColor.GRAY + " to rankup.");
    }
}