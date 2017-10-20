package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow players to change their local time.
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandPTime extends PlayerCommand {

    public CommandPTime() {
        super(EnumRank.SIGMA, "<time|sunrise|day|midnight|night|reset>", "Set your local time.", "ptime");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("reset")) {
            p.resetPlayerTime();
            sender.sendMessage(ChatColor.GOLD + "Clock synced.");
        } else if(args[0].equalsIgnoreCase("day")) {
            p.setPlayerTime(1000L, false);
            sender.sendMessage( ChatColor.GOLD + "Time set to day.");
        } else if(args[0].equalsIgnoreCase("night")) {
            p.setPlayerTime(13000L, false);
            sender.sendMessage(ChatColor.GOLD + "Time set to night.");
        } else if(args[0].equalsIgnoreCase("midnight")) {
            p.setPlayerTime(18000L, false);
            sender.sendMessage(ChatColor.GOLD + "Time set to midnight.");
        } else if(args[0].equalsIgnoreCase("sunrise")) {
            p.setPlayerTime(22916L, false);
            sender.sendMessage(ChatColor.GOLD + "Time set to sunrise.");
        }
        else {
            p.setPlayerTime(Integer.parseInt(args[0]), false);
            sender.sendMessage(ChatColor.GOLD + "Time set to " + ChatColor.RED + args[0]);
        }
    }
}
