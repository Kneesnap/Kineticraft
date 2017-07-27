package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Wear your held item as a hat.
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandHat extends PlayerCommand {
    public CommandHat() {
        super(EnumRank.SIGMA, "", "Wear your held item as a hat.", "hat");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        EntityEquipment e = player.getEquipment();
        ItemStack held = e.getItemInMainHand();
        e.setItemInMainHand(e.getHelmet());
        e.setHelmet(held);

        sender.sendMessage(ChatColor.GOLD + "Enjoy your new hat.");
    }
}
