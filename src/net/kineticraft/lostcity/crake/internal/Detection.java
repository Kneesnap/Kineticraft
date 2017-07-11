package net.kineticraft.lostcity.crake.internal;

import lombok.Getter;
import net.kineticraft.lostcity.utils.ServerUtils;
import org.bukkit.entity.Player;

/**
 * Represents a player being detected for a hack. Contains data related to the situation.
 * Created by Kneesnap on 7/10/2017.
 */
@Getter
public class Detection {

    private Player player;
    private int tick = ServerUtils.getCurrentTick();

    public Detection(Player player) {
        this.player = player;
    }

    /**
     * Has this detection gone invalid?
     *
     * @return expired
     */
    public boolean hasExpired() {
        return !getPlayer().isOnline();
    }
}
