package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.data.PlayerDeath;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Handles Compass Mechanics.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class CompassMechanics extends Mechanic {

    @EventHandler
    public void onCompassClick(PlayerInteractEvent evt) {
        if (evt.getItem() != null && evt.getItem().getType() == Material.COMPASS)
            pickCompass(evt.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        Core.logInfo(evt.getEntity().getName() + " died at " + Utils.toString(evt.getEntity().getLocation()));
        KCPlayer p = KCPlayer.getWrapper(evt.getEntity());
        p.getDeaths().add(new PlayerDeath(evt), 3);
        p.setLastLocation(evt.getEntity().getLocation());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent evt) {
        updateCompass(evt.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent evt) {
        updateCompass(evt.getPlayer());
    }

    private static void pickCompass(Player player) {
        KCPlayer p = KCPlayer.getWrapper(player);
        if (p.getDeaths().isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "You have no recorded deaths.");
            return;
        }

        int newId = MetadataManager.getValue(player, "compassDeath", 0);
        newId = p.getDeaths().hasIndex(newId + 1) ? newId + 1 : 0;
        MetadataManager.setMetadata(player, "compassDeath", newId);

        player.sendMessage(ChatColor.GRAY + "Compass pointed at your "
                + (newId > 0 ? (newId == 1 ? "second" : "third") + " to " : "") + "last death.");
        updateCompass(player);
    }

    /**
     * Update the location the user's compass points to.
     * Delays itself by a tick to compensate for cases like switching worlds.
     *
     * @param player
     */
    private static void updateCompass(Player player) {
        Location loc = KCPlayer.getWrapper(player).getSelectedDeath();
        if (loc != null)
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> player.setCompassTarget(loc));
    }

    @Override
    public void onJoin(Player player) {
        updateCompass(player);
    }
}
