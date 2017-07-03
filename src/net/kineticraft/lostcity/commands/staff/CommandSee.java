package net.kineticraft.lostcity.commands.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Function;

/**
 * Allow opening of a player's inventories.
 *
 * Created by egoscio on 6/23/17.
 */
public class CommandSee extends StaffCommand {

    public CommandSee() {
        super("<inv|end> <name>", "See a target's inventory or enderchest.", "see");
        autocompleteOnline(1);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        InventoryType type = InventoryType.valueOf(args[0].toUpperCase());
        ((Player) sender).openInventory(type.getSupplier().apply(target));
        sender.sendMessage(ChatColor.GREEN + "Opened " + target.getName() + "'s " + type.getName() + ".");
    }

    @AllArgsConstructor @Getter
    private enum InventoryType {
        INV(Player::getInventory, "inventory"),
        END(Player::getEnderChest, "enderchest");

        private final Function<Player, Inventory> supplier;
        private final String name;
    }
}
