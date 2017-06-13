package net.kineticraft.lostcity.item.event.events;

import lombok.Getter;
import net.kineticraft.lostcity.item.event.ItemEvent;
import net.kineticraft.lostcity.item.event.ItemUsage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Represents clicking an entity, attack or normal click.
 *
 * Created by Kneesnap on 6/12/2017.
 */
@Getter
public class ItemEntityInteractEvent extends ItemEvent {

    private EquipmentSlot hand;

    public ItemEntityInteractEvent(EntityDamageByEntityEvent evt) {
        this((Player) evt.getDamager(), EquipmentSlot.HAND, ItemUsage.LEFT_CLICK_ENTITY, evt);
    }

    public ItemEntityInteractEvent(PlayerInteractEntityEvent evt) {
        this(evt.getPlayer(), evt.getHand(), ItemUsage.RIGHT_CLICK_ENTITY, evt);
    }

    public ItemEntityInteractEvent(Player p, EquipmentSlot slot,  ItemUsage usage,  Event evt) {
        super(p, p.getInventory().getItem(slot), usage, evt);
        this.hand = slot;
    }

    @Override
    protected void handle() {
        getPlayer().getInventory().setItem(hand, getResult());
    }
}