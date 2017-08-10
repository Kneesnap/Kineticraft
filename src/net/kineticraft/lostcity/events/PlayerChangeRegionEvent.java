package net.kineticraft.lostcity.events;

import lombok.Getter;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * An event that fires when the worldguard region a player is in changes.
 * Created by Kneesnap on 8/9/2017.
 */
@Getter
public class PlayerChangeRegionEvent extends PlayerEvent {
    @Getter private static HandlerList handlerList = new HandlerList();
    private String regionFrom;
    private String regionTo;

    public PlayerChangeRegionEvent(PlayerMoveEvent evt) {
        super(evt.getPlayer());
        this.regionFrom = Utils.getRegion(evt.getFrom());
        this.regionTo = Utils.getRegion(evt.getTo());
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
