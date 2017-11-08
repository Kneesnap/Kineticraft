package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Set the gear of an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.LEATHER_HELMET)
public class ActionEntityGear extends ActionEntity {
    private JsonMap<ItemStack> items = new JsonMap<>(ItemStack.class);

    @Override
    public void execute() {
        items.toEnumMap(EquipmentSlot.class).forEach((k, v) -> Utils.setItem(getLivingEntity(), k, v));
    }

    @Override
    public String toString() {
        return items.size() + " Items" + super.toString();
    }
}
