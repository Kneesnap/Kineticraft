package net.kineticraft.lostcity.item.items;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.item.event.ItemListener;
import net.kineticraft.lostcity.item.event.ItemUsage;
import net.kineticraft.lostcity.item.event.events.ItemInteractEvent;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Armor Stand that has modifiers, such as being small, or having arms.
 * Created by Kneesnap on 6/12/2017.
 */
@Getter @Setter
public class ItemArmorStand extends ItemWrapper implements Listener {

    private boolean small;
    private boolean arms;

    private static ItemArmorStand placing;

    public ItemArmorStand() {
        this(Utils.nextBool(), Utils.nextBool());
    }

    public ItemArmorStand(boolean small, boolean arms) {
        super(ItemType.ARMOR_STAND);
        setSmall(small);
        setArms(arms);
    }

    public ItemArmorStand(ItemStack item) {
        super(item);
        setSmall(getTagBoolean("small"));
        setArms(getTagBoolean("arms"));
    }

    @ItemListener(ItemUsage.RIGHT_CLICK_BLOCK)
    public void onInteract(ItemInteractEvent evt) {
        placing = this;
        evt.getEvent().setCancelled(false);
    }

    @Override
    public ItemStack getRawStack() {
        return new ItemStack(Material.ARMOR_STAND);
    }

    @Override
    public void updateItem() {
        setTagBoolean("small", isSmall());
        setTagBoolean("arms", isArms());

        if (isSmall())
            addLore("+Small");

        if (isArms())
            addLore("+Arms");
    }

    @EventHandler
    public void onArmorSpawn(CreatureSpawnEvent evt) {
        if (evt.getEntityType() != EntityType.ARMOR_STAND || placing == null)
            return;

        ArmorStand as = (ArmorStand) evt.getEntity();
        as.setArms(placing.isArms());
        as.setSmall(placing.isSmall());
        placing = null;
    }
}
