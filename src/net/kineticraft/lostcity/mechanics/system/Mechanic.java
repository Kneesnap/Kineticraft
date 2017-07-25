package net.kineticraft.lostcity.mechanics.system;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Mechanic - Basic game mechanic.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class Mechanic implements Listener {

    public Mechanic() {

    }

    /**
     * Calls when the server starts.
     */
    public void onEnable() {

    }

    /**
     * Calls when the server shuts down.
     */
    public void onDisable() {

    }

    /**
     * Calls when a player enters the world. (After authentication, ban checks, etc.)
     * @param player
     */
    public void onJoin(Player player) {

    }

    /**
     * Calls when a player disconnects.
     * We use this instead of listening for a bukkit event since multiple event can call.
     */
    public void onQuit(Player player) {

    }
}
