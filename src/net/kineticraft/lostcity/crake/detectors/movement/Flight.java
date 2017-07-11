package net.kineticraft.lostcity.crake.detectors.movement;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.crake.detectors.Detector;
import net.kineticraft.lostcity.crake.internal.Detection;
import net.kineticraft.lostcity.crake.internal.DetectionStore;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A rudementary flight detector.
 * Based on Archelaus' fly detector.
 * Detects descent and ascent.
 *
 * Created by Kneesnap on 7/10/2017.
 */
public class Flight extends Detector {

    private DetectionStore<Detection> detections = new DetectionStore<>("might be flying");

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Player p = evt.getPlayer();
        Block b = p.getLocation().getBlock();
        if (isImmune(p) // Verify we should get an alert from this player.
                || Utils.isSameBlock(evt.getFrom(), evt.getTo()) // Check they've moved at least to the next block.
                || !checkNearby(b.getRelative(BlockFace.DOWN))) // Check if a block is nearby them.
            return;

        double yDif = evt.getTo().getY() - evt.getFrom().getY();
        double ascentMax = 0.8D;
        if (p.hasPotionEffect(PotionEffectType.JUMP))
            ascentMax += (double) p.getPotionEffect(PotionEffectType.JUMP).getAmplifier() / 10;

        detections.detect(new Detection(p),
                yDif == 0 && !checkNearby(evt.getFrom(), Material.WATER_LILY, Material.CARPET), // Hover.
                yDif > ascentMax, // If they sharply ascend.
                yDif == -.125D); // Glide (Unsure if this works.)


    }

    /**
     * Check if there is a solid block nearby the block.
     * @param bk
     * @return nearbyBlock
     */
    private static boolean checkNearby(Block bk) {
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++)
                if (Utils.isSolid(bk.getLocation().clone().add(x, 0, z).getBlock()))
                    return false;
        return true;
    }

    /**
     * Check if there is a block on the same Y with a x or z within 1 difference of this location of a given material.
     * @param loc
     * @param mat
     * @return nearby
     */
    private static boolean checkNearby(Location loc, Material... mat) {
        List<Material> types = Arrays.asList(mat);
        return Stream.of(BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.EAST)
                .map(bf -> loc.getBlock().getRelative(bf).getType()).anyMatch(types::contains);
    }

    /**
     * Is the player in a state or situation that we should deem them not flying?
     * @param player
     * @return
     */
    private static boolean isImmune(Player player) {
        return player.getGameMode() != GameMode.SURVIVAL
                || Utils.getRank(player).isAtLeast(EnumRank.MEDIA)
                || player.isGliding()
                || player.hasPotionEffect(PotionEffectType.LEVITATION);
    }
}
