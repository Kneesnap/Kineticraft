package net.kineticraft.lostcity.guis.staff;

import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;

/**
 * Allows editting of Merchants.
 * Incomplete.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class GUIMerchantEditor extends GUI {

    private Merchant merchant;

    public GUIMerchantEditor(Player player, Merchant m) {
        super(player, "Merchant Editor", m.getRecipeCount());
        this.merchant = m;
    }

    @Override
    public void addItems() {
        merchant.getRecipes().forEach(mr -> {
            for (int i = 0; i < mr.getIngredients().size(); i++) {
                final int index = i;
                ItemStack item = mr.getIngredients().get(i);
                addItem(item).anyClick(e -> new GUIItemEditor(getPlayer(), item, iw -> mr.getIngredients().set(index, iw.generateItem())));
            }

            rowSlot(3);
            addItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "Trade", "<-- Ingredients", "--> Result").setColor(DyeColor.LIME);
            addItem(mr.getResult()).anyClick(e -> new GUIItemEditor(getPlayer(), mr.getResult(),
                    iw -> ReflectionUtil.setField(mr, "result", iw.generateItem())));

            skipSlots(1);
            addItem(Material.BARRIER, ChatColor.RED + "Click here to delete recipe.").anyClick(e -> {
                merchant.getRecipes().remove(mr);
                reconstruct();
            });

            nextRow();
        });
    }
}
