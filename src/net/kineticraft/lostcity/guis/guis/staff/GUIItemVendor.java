package net.kineticraft.lostcity.guis.guis.staff;

import net.kineticraft.lostcity.guis.PagedGUI;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spawn any simple item.
 *
 * Created by Kneesnap on 6/12/2017.
 */
public class GUIItemVendor extends PagedGUI {

    public GUIItemVendor(Player player) {
        super(player, "Item Vendor", fitSize(getItems()));
    }

    @Override
    public void addItems() {
        for (ItemStack i : getItems())
            addItem(i).anyClick(e -> Utils.giveItem(getPlayer(), i));
        super.addItems();
    }


    private static List<ItemStack> getItems() {
        return Arrays.stream(ItemType.values()).map(ItemType::makeSimple).filter(i -> i != null)
                .map(ItemWrapper::generateItem).collect(Collectors.toList());
    }
}
