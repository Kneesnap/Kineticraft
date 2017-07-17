package net.kineticraft.lostcity.crake.detectors.misc;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.crake.detectors.Detector;
import net.kineticraft.lostcity.crake.internal.Detection;
import net.kineticraft.lostcity.crake.internal.DetectionStore;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

/**
 * A simple Xray path detector.
 * Created by Kneesnap on 7/16/2017.
 */
public class Xray extends Detector {

    private XrayPath blocks = new XrayPath();
    private DetectionStore<BrokenBlock> elevation = new DetectionStore<>(15, 5, "is mining vertically");

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent evt) {
        Location at = evt.getBlock().getLocation();
        if (at.getY() > 20 || !at.getWorld().equals(Core.getMainWorld()))
            return;

        // Remember mined blocks.
        boolean recentVein = MetadataManager.hasCooldown(evt.getPlayer(), "lastDiamond");
        boolean isDiamond = evt.getBlock().getType() == Material.DIAMOND_ORE;
        BrokenBlock bk = new BrokenBlock(evt.getPlayer(), evt.getBlock());
        if (!(isDiamond && recentVein)) { // Record the diamond unless its part of an already added vein.
            blocks.add(bk);
            if (recentVein)
                blocks.detect(bk); // Only detect the mine analyser if they're actually mining.
        }

        // Handle suspicious ascending / descending.
        if (elevation.getDetections(evt.getPlayer()).stream().noneMatch(b -> b.getLocation().getY() == at.getY()))
            elevation.detect(bk);
    }

    private static class XrayPath extends DetectionStore<BrokenBlock> {
        public XrayPath() {
            super(60 * 30, 5, "might be xraying");
        }

        @Override
        public boolean isTriggered(Player player) {
            if (!super.isTriggered(player))
                return false;

            Map<String, Integer> veinMap = new HashMap<>();
            getDetections(player).stream().map(BrokenBlock::getLocation).forEach(loc -> {
                String x = loc.getX() + ",";
                String z = "," + loc.getZ();
                veinMap.put(x, veinMap.getOrDefault(x, 0) + 1);
                veinMap.put(z, veinMap.getOrDefault(z, 0) + 1);
            });

            return veinMap.keySet().stream().filter(k -> veinMap.get(k) >= 2).count() > 2 // Abnormal amount of veins. (Direction changes)
                    || getCount(player, Material.DIAMOND) * 100 > getCount(player, Material.STONE); // Ratio of stone to diamonds is too high.
        }

        private int getCount(Player player, Material mat) {
            return (int) getDetections(player).stream().filter(b -> b.getType() == mat).count();
        }
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
