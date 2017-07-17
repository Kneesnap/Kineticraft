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
     * Returns whether or not this tick has expired based on time only.
     * If it has, it executes onExpire.
     * @return old
     */
    public boolean isOld(int expireTicks) {
        boolean old = expireTicks >= 0 && ServerUtils.getCurrentTick() > getTick() + expireTicks;
        if (old)
            onExpire();
        return old;
    }

    /**
     * Has this detection gone invalid?
     * @return expired
     */
    public boolean hasExpired() {
        return !getPlayer().isOnline();
    }

    /**
     * Calls when this detection is deleted because it was too old.
     */
    public void onExpire() {

    }
}
