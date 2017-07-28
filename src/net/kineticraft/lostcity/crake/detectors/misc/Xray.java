package net.kineticraft.lostcity.crake.detectors.misc;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.crake.detectors.Detector;
import net.kineticraft.lostcity.crake.internal.Detection;
import net.kineticraft.lostcity.crake.internal.DetectionStore;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * A simple Xray path detector.
 * Created by Kneesnap on 7/16/2017.
 */
public class Xray extends Detector {

    private DetectionStore<BrokenBlock> blocks = new DetectionStore<>(3600, 5, "might be xraying", false);
    private DetectionStore<BrokenBlock> elevation = new DetectionStore<>(15, 6, "is mining vertically");

    private static final int STONE_MULT = 1000;
    private static final int TIME_MULT = 5000;

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent evt) {
        Location at = evt.getBlock().getLocation();
        if (at.getY() > 20 || !at.getWorld().equals(Core.getMainWorld()))
            return;

        // Remember mined blocks.
        BrokenBlock bk = new BrokenBlock(evt.getPlayer(), evt.getBlock());
        blocks.add(bk);
        if ((bk.getType() == Material.DIAMOND_ORE || Utils.randChance(20))
                && (getBlockScore(evt.getPlayer()) >= 20 || getTimeScore(evt.getPlayer()) >= 20))
                blocks.detect(bk);

        // Handle suspicious ascending / descending.
        if (elevation.getDetections(evt.getPlayer()).stream().noneMatch(b -> b.getLocation().getY() == at.getY()))
            elevation.detect(bk);
    }

    /**
     * Get the "Xray Score" of a given player's situation from blocks mined.
     * @param player
     * @return score
     */
    private int getBlockScore(Player player) {
        return (getCount(player, Material.DIAMOND_ORE) * STONE_MULT) / (getCount(player, Material.STONE) + (STONE_MULT / 2));
    }

    /**
     * Get the "Xray Score" of a given player's situation from diamonds / time.
     * @param player
     * @return score
     */
    private int getTimeScore(Player player) {
        return (getCount(player, Material.DIAMOND_ORE) * TIME_MULT) / Math.max(1000, blocks.firstDetection(player));
    }


    /**
     * Get the number of blocks of a given material mined by the player recently.
     * @param player
     * @param mat
     * @return count
     */
    private int getCount(Player player, Material mat) {
        return (int) blocks.getDetections(player).stream().filter(b -> b.getType() == mat).count();
    }

    @Getter
    private static class BrokenBlock extends Detection {
        private Material type;
        private Location location;

        public BrokenBlock(Player player, Block mined) {
            super(player);
            this.type = mined.getType();
            this.location = mined.getLocation();
        }
    }
}
