package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Allow players to condense their items.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandCondense extends PlayerCommand {

    private static final List<Material> BLOCKS = Arrays.asList(Material.REDSTONE, Material.COAL, Material.EMERALD,
            Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND_BLOCK, Material.MELON, Material.SLIME_BALL,
            Material.DIAMOND);

    public CommandCondense() {
        super(EnumRank.ALPHA, "", "Condense all items in your inventory.", "stack", "condense");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (BLOCKS.contains(item.getType())) {
                ItemStack add = new ItemStack(Material.valueOf(item.getType().name().split("_")[0] + "_BLOCK"), 0);
                while (item.getAmount() >= 9) {
                    item.setAmount(item.getAmount() - 9);
                    add.setAmount(add.getAmount() + 1);
                }

                if (add.getAmount() > 0)
                    player.getInventory().addItem(add);
            }

            if (item.getAmount() > 0)
                player.getInventory().addItem(item);
        }
    }
}
