package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Spawn a claim shovel.
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandShovel extends PlayerCommand {

    public CommandShovel() {
        super("", "Spawn a shovel for claiming blocks.", "shovel");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player.getInventory().contains(Material.GOLD_SPADE)) {
            sender.sendMessage(ChatColor.RED + "You already have a shovel.");
            return;
        }

        if (MetadataManager.updateCooldown(player, "shovel", 20 * 60 * 10)) // 10 minute cooldown
            return;

        Utils.giveItem(player, ItemManager.makeClaimShovel());
        sender.sendMessage(ChatColor.GREEN + "Shovel spawned.");
    }
}
