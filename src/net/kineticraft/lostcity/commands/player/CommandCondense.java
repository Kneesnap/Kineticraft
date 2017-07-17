package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allow players to condense their items.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandCondense extends PlayerCommand {

    private static final List<Material> BLOCKS = Arrays.asList(Material.REDSTONE, Material.COAL, Material.EMERALD,
            Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.MELON, Material.SLIME_BALL);
    private static final Map<ItemStack, Material> CONDENSE = new HashMap<>();

    public CommandCondense() {
        super(EnumRank.ALPHA, "", "Condense all items in your inventory.", "stack", "condense");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
            if (item == null || item.getType() == Material.AIR)
                continue;

            ItemStack c = CONDENSE.keySet().stream().filter(itm -> itm.getType() == item.getType()).findAny().orElse(null);
            if (c != null) {
                ItemStack add = new ItemStack(CONDENSE.get(c), 0);
                while (item.getAmount() >= c.getAmount()) { // If there's enough to equal one condensed item.
                    item.setAmount(item.getAmount() - c.getAmount()); //Remove the items needed to create a condensed item.
                    add.setAmount(add.getAmount() + 1); // Add 1 to the condensed item.
                }

                if (add.getAmount() > 0) // Give the player the condensed item.
                    player.getInventory().addItem(add);
            }

            if (item.getAmount() > 0)
                player.getInventory().addItem(item);
        }
    }

    static {
        CONDENSE.put(new ItemStack(Material.INK_SACK, 9, DyeColor.BLUE.getDyeData()), Material.LAPIS_BLOCK); // Lapis

        // Take all of the blocks and add them to the condense list.
        for (Material m : BLOCKS)
            CONDENSE.put(new ItemStack(m, 9), Material.valueOf(m.name().split("_")[0] + "_BLOCK"));
    }
}
