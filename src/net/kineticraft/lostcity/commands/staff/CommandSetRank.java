package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.data.QueryTools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandSetRank - Set the rank of a player.
 *
 * Created by Kneesnap on 5/30/2017.
 */
public class CommandSetRank extends PlayerCommand {

    public CommandSetRank() {
        super(EnumRank.MOD, false, "<player> <rank>", "Set a player's rank.", "setrank");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        EnumRank newRank = EnumRank.getByName(args[1]);
        EnumRank myRank = sender instanceof Player ? KCPlayer.getWrapper((Player) sender).getRank() : EnumRank.DEV;

        if (newRank == null) {
            sender.sendMessage(ChatColor.RED + "Unknown rank '" + args[1] + "'.");
            return;
        }

        if (!myRank.isAtLeast(newRank)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to set players to this rank.");
            return;
        }

        QueryTools.getData(args[0], kcPlayer ->  {

            if (!myRank.isAtLeast(kcPlayer.getRank())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to manage this user.");
                return;
            }

            Core.warn(sender.getName() + " updated " + kcPlayer.getUsername() + "'s rank to " + newRank.getName() + ".");
            kcPlayer.setRank(newRank);
            sender.sendMessage(ChatColor.GREEN + "Updated " + kcPlayer.getUsername() + "'s rank to "
                    + newRank.getName() + ".");
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }

    @Override
    protected void showUsage(CommandSender sender,  String args) {
        super.showUsage(sender, args);

        EnumRank myRank = sender instanceof Player ? KCPlayer.getWrapper((Player) sender).getRank() : EnumRank.DEV;

        String ranks = "";
        for (EnumRank rank : EnumRank.values())
            if (myRank.isAtLeast(rank))
                ranks += " " + rank.getColor() + ChatColor.BOLD + rank.getName();

        sender.sendMessage(ChatColor.RED + "Ranks:" + ranks);
    }
}
