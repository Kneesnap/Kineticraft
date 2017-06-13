package net.kineticraft.lostcity.item.event.events;

import lombok.Getter;
import net.kineticraft.lostcity.item.event.ItemEvent;
import net.kineticraft.lostcity.item.event.ItemUsage;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Represents a player clicking with an item, fires off PlayerInteractEvent.
 *
 * Created by Kneesnap on 6/11/2017.
 */
@Getter
public class ItemInteractEvent extends ItemEvent {

    public ItemInteractEvent(PlayerInteractEvent evt) {
        super(evt.getPlayer(), evt.getItem(), ItemUsage.valueOf(evt.getAction().name()), evt);
    }

    @Override
    public PlayerInteractEvent getEvent() {
        return (PlayerInteractEvent) super.getEvent();
    }

    @Override
    protected void handle() {
        getPlayer().getInventory().setItem(getEvent().getHand(), getResult());
    }
}
