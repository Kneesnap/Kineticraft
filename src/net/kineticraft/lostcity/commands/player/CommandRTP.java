package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.mechanics.MetadataManager;
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

        if (MetadataManager.hasCooldown(player, MetadataManager.Metadata.RTP))
            return;

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You may only randomly teleport in the overworld.");
            return;
        }

        MetadataManager.setCooldown(player, MetadataManager.Metadata.RTP, 20 * 60 * 10);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 0 15000 false " + player.getName());
    }
}
