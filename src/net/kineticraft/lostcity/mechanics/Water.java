package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Water Mechanics
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class Water extends Mechanic {

    private static final PotionEffectType POTION = PotionEffectType.WATER_BREATHING;

    @EventHandler
    public void onWaterTraverse(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        if (evt.getTo().getBlock().isLiquid()) {
            if (!player.hasPotionEffect(POTION))
                player.addPotionEffect(new PotionEffect(POTION, Integer.MAX_VALUE, 2));
        } else if (evt.getFrom().getBlock().isLiquid()) {
            disableSpeed(player);
        }
    }

    @Override
    public void onQuit(Player player) {
        disableSpeed(player);
    }

    /**
     * Disable all water effects for the player.
     * @param player
     */
    public void disableSpeed(Player player) {
        PotionEffect pe = player.getPotionEffect(POTION);
        if (pe != null && pe.getDuration() >= 30 * 60 * 20) // Remove water breathing, only if it's longer than a potion could be.
            player.removePotionEffect(POTION);
    }
}
