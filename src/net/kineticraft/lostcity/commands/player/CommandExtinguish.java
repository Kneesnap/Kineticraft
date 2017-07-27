package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Stop, drop, and roll.
 * Created by Kneesnap on 7/12/2017.
 */
public class CommandExtinguish extends PlayerCommand {

    public CommandExtinguish() {
        super(EnumRank.BETA, "", "Extinguish yourself", "ext");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (p.getFireTicks() < 0) {
            sender.sendMessage(ChatColor.GRAY + "You are not on fire.");
            return;
        }

        p.setFireTicks(0);
        p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
        sender.sendMessage(ChatColor.GRAY + "The fire has been extinguished.");
    }
}
