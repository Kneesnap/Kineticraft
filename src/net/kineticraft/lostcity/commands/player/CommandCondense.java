package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Allow players to condense their items.
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
            if (Utils.isAir(item))
                continue;

            ItemStack c = CONDENSE.keySet().stream().filter(item::isSimilar).findAny().orElse(null);
            List<ItemStack> condenseResults = new ArrayList<>();
            if (c != null) {
                ItemStack add = new ItemStack(CONDENSE.get(c), 0);
                while (item.getAmount() >= c.getAmount()) { // If there's enough to equal one condensed item.
                    item.setAmount(item.getAmount() - c.getAmount()); //Remove the items needed to create a condensed item.
                    add.setAmount(add.getAmount() + 1); // Add 1 to the condensed item.
                }
                condenseResults.add(add);
            }

            addItem(player, item, i); // Add the leftover ingredient.
            condenseResults.forEach(condense -> addItem(player, condense, -1));
        }
        player.updateInventory();
    }

    /**
     * Add an item to the player's inventory, using a stacksize of 64.
     * @param player
     * @param itemStack
     * @param fallbackSlot
     */
    private void addItem(Player player, ItemStack itemStack, int fallbackSlot) {
        if (itemStack.getAmount() <= 0)
            return;

        List<ItemStack> items = new ArrayList<>(player.getInventory().all(itemStack.getType()).values());
        if (fallbackSlot >= 0) // Don't handle the slot the item is already in.
            items.remove(player.getInventory().getItem(fallbackSlot));

        while (itemStack.getAmount() > 0 && !items.isEmpty()) {
            ItemStack addTo = items.remove(0);
            if (!addTo.isSimilar(itemStack))
                continue; // If the NBT tags don't match, don't add to this item.

            int difference = Math.min(itemStack.getAmount(), 64 - addTo.getAmount());
            addTo.setAmount(addTo.getAmount() + difference);
            itemStack.setAmount(itemStack.getAmount() - difference);
        }

        //Add the leftover stack to the inventory.
        int newSlot = fallbackSlot >= 0 ? fallbackSlot : player.getInventory().firstEmpty();
        if (newSlot >= 0) {
            player.getInventory().setItem(newSlot, itemStack);
        } else {
            Utils.giveItem(player, itemStack);
        }
    }

    static {
        //noinspection deprecation
        CONDENSE.put(new ItemStack(Material.INK_SACK, 9, DyeColor.BLUE.getDyeData()), Material.LAPIS_BLOCK); // Lapis

        // Take all of the blocks and add them to the condense list.
        for (Material m : BLOCKS)
            CONDENSE.put(new ItemStack(m, 9), Material.valueOf(m.name().split("_")[0] + "_BLOCK"));
    }
}
