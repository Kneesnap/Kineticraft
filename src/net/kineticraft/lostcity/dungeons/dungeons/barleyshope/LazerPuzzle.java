package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple lazer puzzle.
 * Created by Kneesnap on 7/29/2017.
 */
@Getter
public class LazerPuzzle extends Puzzle {
    private BukkitTask traceTask;
    private static final int PER_BLOCK = 5;
    private List<Block> repeats = new ArrayList<>();

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockClick(PlayerInteractEvent evt, Block bk, boolean rightClick) {
        if (!canTrigger() || MetadataManager.updateCooldownSilently(evt.getPlayer(), "lazerBk", 2))
            return;

        int y = getGateLocation().getBlockY();
        Block above = bk.getRelative(BlockFace.UP);
        if (isPuzzle(bk, y, Material.WOOL) && above.getType() == Material.AIR && rightClick) {
            above.setType(Material.DIODE_BLOCK_OFF);
            above.setData((byte) 0);
        }

        // Remove the next
        if (isPuzzle(bk, y + 1, Material.DIODE_BLOCK_OFF)) {
            if (!rightClick) {
                bk.setType(Material.AIR);
                return;
            }

            bk.setData((byte) (bk.getData() >= 3 ? 0 : bk.getData() + 1));
            evt.setCancelled(true);
        }
    }

    @PuzzleTrigger
    public void fireLazer(CommandBlock block) {
        getRepeats().clear();
        Block bk = block.getBlock().getRelative(BlockFace.UP);

        BlockFace[] direction = new BlockFace[] {Utils.getDirection(bk)};
        Location lazer = bk.getLocation().add(.5, .15, .5);

        // Shoot the lazer.
        traceTask = addTimerTask(() -> {
            lazer.getWorld().spawnParticle(Particle.REDSTONE, lazer, 1, 0, 0, 0, 0); // Draw trail.

            Block last = lazer.getBlock();
            BlockFace d = direction[0];
            lazer.add(d.getModX() / ((double) PER_BLOCK), 0, d.getModZ() / ((double) PER_BLOCK)); // Move the lazer along its path.
            Block b = lazer.getBlock();

            if (!last.equals(b)) {
                if (b.getType() == Material.AIR)
                    return;

                if (b.getType() == Material.DIODE_BLOCK_OFF && !getRepeats().contains(b)) { // Change direction.
                    getRepeats().add(b); // Don't allow infinite loops.
                    BlockFace newDirection = Utils.getDirection(b);
                    if (newDirection.getOppositeFace() != d) { // Cannot activate repeater from the direction it faces.
                        lazer.add(d.getModX() * 0.5, 0, d.getModZ() * 0.5); // Center lazer.
                        activateRepeater(b);
                        direction[0] = newDirection; // Update direction.
                        return;
                    }
                }

                // We've hit a wall.
                traceTask.cancel(); // Stop trying to trace the lazer.
                traceTask = null;
                if (b.getType() == Material.BEACON) {// This wall is actually the goal block.
                    complete();
                } else { // They failed.
                    b.getWorld().spawnEntity(b.getLocation().add(0, 1, 0), EntityType.ZOMBIE);
                }
            }
        }, 1L);
    }

    /**
     * Loosely determines if a block is a part of the puzzle, and the correct Y level.
     * @param bk
     * @param yLevel
     * @param type
     * @return isPuzzle
     */
    private boolean isPuzzle(Block bk, int yLevel, Material type) {
        Location l = bk.getLocation();
        return l.distance(getGateLocation()) <= 50 && l.getBlockY() == yLevel && bk.getType() == type;
    }

    @SuppressWarnings("deprecation")
    private void activateRepeater(Block bk) {
        setFakeBlock(bk, Material.DIODE_BLOCK_ON);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> setFakeBlock(bk, null), PER_BLOCK);
    }

    @Override
    protected boolean canTrigger() {
        return super.canTrigger() && traceTask == null;
    }
}
