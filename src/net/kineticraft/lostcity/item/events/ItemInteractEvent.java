package net.kineticraft.lostcity.item.events;

/**
 * Represents a player clicking with an item, fires off PlayerInteractEvent.
 *
 * Created by Kneesnap on 6/11/2017.
 */
public class ItemInteractEvent {


    public interface ItemInteractListener {
        public void onInteract(ItemInteractEvent evt);
    }
}
