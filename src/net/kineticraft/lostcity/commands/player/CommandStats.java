package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * View stats of another player.
 * Created by Kneesnap on 7/24/2017.
 */
public class CommandStats extends PlayerCommand {
    public CommandStats() {
        super("[player]", "View player statistics.", "stats");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        QueryTools.getData(args.length > 0 ? args[0] : sender.getName(), player -> {
            sender.sendMessage(ChatColor.GRAY + "Statistics for " + ChatColor.GREEN + player.getUsername() + ChatColor.GRAY + ":");
            sendValue(sender, "Teleport ID", player.getAccountId());
            sendValue(sender, "Rank", player.getTemporaryRank().getFullName());
            sendValue(sender, "Playtime", Utils.formatTimeFull(player.getSecondsPlayed() * 1000));
            sendValue(sender, "Monthly Votes", player.getMonthlyVotes());
            sendValue(sender, "Total Votes", player.getTotalVotes());
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
