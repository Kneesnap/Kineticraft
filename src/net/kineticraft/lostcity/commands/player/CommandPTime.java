package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandPTime extends PlayerCommand {

    public CommandPTime() {
        super(EnumRank.GAMMA, true, "<time|reset>", "Set your local time.", "ptime");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("reset")) {
            p.resetPlayerTime();
            sender.sendMessage(ChatColor.GOLD + "Clock synced.");
        } else {
            p.setPlayerTime(Integer.parseInt(args[0]), false);
            sender.sendMessage(ChatColor.GOLD + "Time set to " + ChatColor.RED + args[0]);
        }
    }
}
