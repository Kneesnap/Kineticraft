package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * A simple lazer puzzle.
 * Created by Kneesnap on 7/29/2017.
 */
@Getter
public class LazerPuzzle extends Puzzle {
    private BukkitTask traceTask;
    private static final double PER_BLOCK = 5;

    public LazerPuzzle() {
        super(new Location(null, -113, 12, 44));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockClick(Block bk, boolean rightClick) {
        if (!canTrigger())
            return;

        int y = getRedstoneBlock().getLocation().getBlockY() + 2;
        Block above = bk.getRelative(BlockFace.UP);
        if (isPuzzle(bk, y - 1, Material.WOOL) && above.getType() == Material.AIR && !rightClick) {
            above.setType(Material.DIODE_BLOCK_OFF);
            above.setData((byte) 0);
        }

        // Remove the next
        if (isPuzzle(bk, y, Material.DIODE_BLOCK_OFF) && rightClick) {
            int next = bk.getData() + 1;
            if (next > 3) {
                bk.setType(Material.AIR);
            } else {
                bk.setData((byte) next);
            }
        }
    }

    @PuzzleTrigger
    public void fireLazer(CommandBlock block) {
        Block bk = block.getBlock();

        BlockFace[] direction = new BlockFace[] {Utils.getDirection(bk)};
        Location lazer = bk.getLocation().add(.5, .15, .5);

        // Shoot the lazer.
        traceTask = addTimerTask(() -> {
            lazer.getWorld().spawnParticle(Particle.REDSTONE, lazer, 1, 0, 0, 0, 0); // Draw trail.

            Block last = lazer.getBlock();
            BlockFace d = direction[0]; //
            lazer.add(d.getModX() / PER_BLOCK, 0, d.getModZ() / PER_BLOCK); // Move the lazer along its path.
            Block b = lazer.getBlock();

            if (!last.equals(b)) {
                if (b.getType() == Material.AIR)
                    return;

                if (b.getType() == Material.DIODE_BLOCK_OFF) { // Change direction.
                    BlockFace newDirection = Utils.getDirection(b);
                    if (newDirection != d && newDirection.getOppositeFace() != d) { // Power must enter sideways.
                        lazer.add(d.getModX() * 0.5, 0, d.getModZ() * 0.5); // Center lazer.
                        setRepeater(b, Material.DIODE_BLOCK_ON, (long) PER_BLOCK);
                        direction[0] = newDirection; // Update direction.
                        return;
                    }
                }

                // We've hit a wall.
                traceTask.cancel(); // Stop trying to trace the lazer.
                traceTask = null;
                if (b.getType() == Material.BEACON) // This wall is actually the goal block.
                    complete();
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
        return l.distance(getRedstoneBlock().getLocation()) <= 50 && l.getBlockY() == yLevel && bk.getType() == type;
    }

    @SuppressWarnings("deprecation")
    private void setRepeater(Block bk, Material mat, long reset) {
        List<Player> players = getDungeon().getPlayers();
        players.forEach(p -> p.sendBlockChange(bk.getLocation(), mat, bk.getData()));
        if (reset > 0)
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> setRepeater(bk, bk.getType(), -1), reset);
    }

    @Override
    protected boolean canTrigger() {
        return super.canTrigger() && traceTask == null;
    }
}
