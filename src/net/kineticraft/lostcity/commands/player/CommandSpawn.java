package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport to spawn.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandSpawn extends PlayerCommand {
    public CommandSpawn() {
        super("", "Teleport to spawn", "spawn");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (Utils.getRank(sender).isAtLeast(EnumRank.TRIAL) && args.length > 0) {
            if (Utils.isVisible(sender, args[0])) {
                Bukkit.getPlayer(args[0]).teleport(Core.getMainWorld().getSpawnLocation());
                sender.sendMessage(ChatColor.GOLD + "Teleported.");
            }
            return;
        }

        Utils.teleport((Player) sender, "Spawn", Core.getMainWorld().getSpawnLocation());
    }
}
