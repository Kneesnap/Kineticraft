package net.kineticraft.lostcity.guis.guis.staff;

import net.kineticraft.lostcity.guis.GUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
            mr.getIngredients().forEach(this::addItem);
            rowSlot(3);
            addItem(mr.getResult());

            skipSlots(1);
            addItem(Material.BARRIER, ChatColor.RED + "Click here to delete recipe.").anyClick(e -> {
                merchant.getRecipes().remove(mr);
                reconstruct();
            });
        });
    }
}
