package net.kineticraft.lostcity.guis.guis.staff;

import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Spawn any simple item.
 *
 * Created by Kneesnap on 6/12/2017.
 */
public class GUIItemVendor extends GUIItemPicker {

    public GUIItemVendor(Player player) {
        super(player, Arrays.stream(ItemType.values()).map(ItemType::makeSimple).map(ItemWrapper::generateItem)
                .collect(Collectors.toList()), i -> Utils.giveItem(player, i));
    }
}
