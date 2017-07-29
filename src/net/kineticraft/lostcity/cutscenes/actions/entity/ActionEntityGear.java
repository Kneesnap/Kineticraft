package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Set the gear of an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.LEATHER_HELMET)
public class ActionEntityGear extends ActionEntity {
    private JsonMap<ItemStack> items = new JsonMap<>();

    @Override
    public void execute(CutsceneEvent event) {
        LivingEntity e = getLivingEntity(event);
        items.toEnumMap(EquipmentSlot.class).forEach((k, v) -> Utils.setItem(e, k, v));
    }
}
