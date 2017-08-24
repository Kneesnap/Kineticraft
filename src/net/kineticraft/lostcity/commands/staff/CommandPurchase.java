package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Lets buycraft indicate someone has donated.
 * Created by Kneesnap on 7/12/2017.
 */
public class CommandPurchase extends StaffCommand {
    public CommandPurchase() {
        super(EnumRank.MOD, "<player> <price>", "Indicate a player has donated.", "purchase");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Core.warn(args[0] + " has donated $" + args[1] + ".");

        QueryTools.getData(args[0], d -> {
            Core.announce("Please thank " + ChatColor.YELLOW + d.getUsername() + ChatColor.RED + " for donating to help keep the server going!");
            if (!d.getRank().isAtLeast(EnumRank.MEDIA)) // Don't demote staff :P
                d.setRank(EnumRank.THETA);
        }, () -> Core.warn("Could not find " + args[0] + " to give donation reward."));
    }

    @Override // BuyCraft uses dispatchCommand
    public boolean registerBukkit() {
        return true;
    }
}
