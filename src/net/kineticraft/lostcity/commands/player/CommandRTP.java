package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Random Teleport
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandRTP extends PlayerCommand {
    public CommandRTP() {
        super("", "Teleport randomly out into the wild", "rtp", "randomtp", "wild", "tprandom");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You may only randomly teleport in the overworld.");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 0 15000 false " + player.getName());

        //TODO: Cooldown
    }
}
