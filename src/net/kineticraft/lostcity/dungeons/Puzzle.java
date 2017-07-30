package net.kineticraft.lostcity.dungeons;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A base dungeon puzzle.
 * TODO: Add timer tasks to a list that can be cancelled.
 * TODO: Verify right dungeon world.
 * Created by Kneesnap on 7/29/2017.
 */
@Getter
public class Puzzle implements Listener {
    private Block placeBlock;
    private boolean complete;
    private List<BukkitTask> tasks = new ArrayList<>();

    public Puzzle(Location place) {
        this.placeBlock = place.getBlock();
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
    }

    /**
     * Complete this puzzle.
     */
    public void complete() {
        //TODO: Play Jingle.
        complete = true;
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::onComplete, 20L);
    }

    /**
     * Called when this puzzle is completed.
     */
    protected void onComplete() {
        getTasks().stream().filter(t -> Bukkit.getScheduler().isCurrentlyRunning(t.getTaskId())
                || Bukkit.getScheduler().isQueued(t.getTaskId())).forEach(BukkitTask::cancel);
        getPlaceBlock().setType(Material.REDSTONE_BLOCK);
    }

    /**
     * Called when the dungeon is removed.
     */
    public void onDungeonRemove() {
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    /**
     * Called when a button is pressed.
     * @param bk
     */
    public void onButtonPress(Block bk) {

    }

    /**
     * Called when a block is punched.
     * @param bk
     */
    public void onBlockPunch(Block bk) {

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getClickedBlock().getType() == Material.STONE_BUTTON)
            onButtonPress(evt.getClickedBlock().getRelative(evt.getBlockFace().getOppositeFace()));

        if (evt.getAction() == Action.LEFT_CLICK_BLOCK)
            onBlockPunch(evt.getClickedBlock());
    }

    /**
     * Register a TimerTask that will be cancelled upon dungeon exit.
     * @param r
     * @param ticks
     * @return
     */
    protected BukkitTask addTimerTask(Runnable r, long ticks) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), r, 0, ticks);
        getTasks().add(task);
        return task;
    }
}
