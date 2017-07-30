package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import lombok.Getter;
import net.kineticraft.lostcity.dungeons.Puzzle;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Directional;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Lazer Puzzle.
 * TODO: Prevent infinity.
 * TODO: Don't allow modification of track after lazer activates.
 * TODO: Center lazer
 * TODO: Fix repeater activation.
 * Created by Kneesnap on 7/29/2017.
 */
@Getter
public class LazerPuzzle extends Puzzle {
    private BukkitTask traceTask;
    private List<Location> particles = new ArrayList<>();
    private static final double PER_BLOCK = 5;

    public LazerPuzzle(Location end) {
        super(end.subtract(0, 1, 0));
        addTimerTask(() -> particles.forEach(this::drawLazer), 10L);
    }

    /**
     * Get the beacon at the end of the puzzle.
     * @return endBlock
     */
    public Block getEndBeacon() {
        return getPlaceBlock().getLocation().clone().add(0, 1, 0).getBlock();
    }

    @Override
    public void onButtonPress(Block bk) {
        Location start = bk.getLocation();
        Location end = getEndBeacon().getLocation();
        if (bk.getType() != Material.DISPENSER || start.getBlockY() != end.getBlockY() || start.distance(end) >= 50 || isTracing() || isComplete())
            return;

        BlockFace[] direction = new BlockFace[] {getDirection(bk)};
        Location lazer = bk.getRelative(direction[0]).getLocation().add(.5, .15,.5);

        traceTask = addTimerTask(() -> {
            Block b = lazer.getBlock(); // Get the block the lazer is currently in.
            if (b.getType() != Material.AIR) { // If we've hit a block.
                if (b.getType() == Material.DIODE_BLOCK_OFF) { // Change direction.
                    direction[0] = getDirection(b);
                    byte d = b.getData();
                    b.setType(Material.DIODE_BLOCK_ON);
                    b.setData(d);
                } else if (b.getType() != Material.DIODE_BLOCK_ON){ // We've hit a wall.
                    traceTask.cancel(); // Stop trying to trace the lazer.
                    traceTask = null;

                    if (b.getType() == Material.BEACON) { // This wall is actually the goal block.
                        complete();
                    } else { // Reset board.
                        getParticles().clear();
                        Utils.getBlocksBetween(getEndBeacon(), bk).stream().filter(r -> r.getType() == Material.DIODE_BLOCK_ON)
                                .forEach(r -> r.setType(Material.DIODE_BLOCK_OFF)); // Reset repeaters.
                    }
                    return;
                }
            } else {
                particles.add(lazer.clone());
                drawLazer(lazer);
            }

            BlockFace d = direction[0]; // Move the lazer along its path.
            lazer.add(d.getModX() / PER_BLOCK, 0, d.getModZ() / PER_BLOCK);
        }, 2L);
    }

    private BlockFace getDirection(Block bk) {
        return ((Directional) bk.getState().getData()).getFacing();
    }

    private void drawLazer(Location loc) {
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0);
    }

    /**
     * Are we currently tracing the redstone path?
     * @return isTracing
     */
    public boolean isTracing() {
        return traceTask != null;
    }
}
