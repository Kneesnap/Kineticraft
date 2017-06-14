package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.wrappers.JsonLocation;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
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
        Location location = evt.getEntity().getLocation();
        Bukkit.getLogger().info(evt.getEntity().getName() + " died at " + Utils.toString(location));
        KCPlayer player = KCPlayer.getWrapper(evt.getEntity());
        player.getDeaths().add(new JsonLocation(location));
        player.getDeaths().trim(3); // Only store 3 deaths
        //TODO: PowerNBT save data.
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

        int newId = MetadataManager.getMetadata(player, MetadataManager.Metadata.COMPASS_DEATH).asInt();
        newId = p.getDeaths().hasIndex(newId + 1) ? newId + 1 : 0;

        player.sendMessage(ChatColor.GRAY + "Compass pointed at your "
                + (newId > 0 ? (newId == 1 ? "second" : "third") + " to " : "") + "last death.");
        MetadataManager.setMetadata(player, MetadataManager.Metadata.COMPASS_DEATH, newId);

        updateCompass(player);
    }

    /**
     * Update the location the user's compass points to.
     * Delays itself by a tick to compensate for cases like switching worlds.
     *
     * @param player
     */
    private static void updateCompass(Player player) {
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            JsonLocation death = KCPlayer.getWrapper(player).getSelectedDeath();
            if (death != null)
                player.setCompassTarget(death.getLocation());
        });
    }

    @Override
    public void onJoin(Player player) {
        updateCompass(player);
    }
}
