package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Mechanic - Basic game mechanic.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class Mechanic implements Listener {

    public Mechanic() {
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
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
     * We use this instead of listening for a bukkit event since multiple events can call.
     */
    public void onQuit(Player player) {
        //TODO: Make sure this doesn't fire when a player fails login.
    }
}
