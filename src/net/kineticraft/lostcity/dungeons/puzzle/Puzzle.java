package net.kineticraft.lostcity.dungeons.puzzle;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A base dungeon puzzle.
 * TODO: Fake blocks should resend their state when the player interacts with them, or comes nearby. (Maybe update it every 10 seconds instead of coming nearby)
 * TODO: Use sign locations.
 * Created by Kneesnap on 7/29/2017.
 */
@Getter
public abstract class Puzzle implements Listener {
    private Location gateLocation;
    private BlockFace gateFace;
    private boolean complete;
    private List<BukkitTask> tasks = new ArrayList<>();
    private Dungeon dungeon;

    private static final Map<Class<? extends Puzzle>, Map<String, Method>> triggers = new HashMap<>();

    public Puzzle(Location place, BlockFace gateFace) {
        this.gateLocation = place;
        this.gateFace = gateFace;
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
    }

    /**
     * Set the dungeon this puzzle is active in.
     * @param d
     */
    public void setDungeon(Dungeon d) {
        this.dungeon = d;
        this.gateLocation = fixLocation(this.gateLocation);
        onInit();
    }

    /**
     * Get a location clone with the correct world.
     * @param l
     * @return loc
     */
    protected Location fixLocation(Location l) {
        return getDungeon().fixLocation(l);
    }

    /**
     * Complete this puzzle.
     */
    public void complete() {
        complete = true;

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> { // Cosmetic delay.
            getGateLocation().getBlock().setType(Material.REDSTONE_BLOCK);
            getDungeon().playCutscene(new PuzzleDoorCutscene(getGateLocation(), getGateFace()));
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::onComplete, 20L);
        }, 35L);
    }

    /**
     * Called when this puzzle is initalized, players have just been teleported into the dungeon.
     */
    protected void onInit() {

    }

    /**
     * Called when this puzzle is completed.
     */
    protected void onComplete() {
        removeTasks();
    }

    /**
     * Called when the dungeon is removed.
     */
    public void onDungeonRemove() {
        removeTasks();
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    protected void removeTasks() {
        getTasks().stream().filter(t -> Bukkit.getScheduler().isCurrentlyRunning(t.getTaskId())
                || Bukkit.getScheduler().isQueued(t.getTaskId())).forEach(BukkitTask::cancel);
        getTasks().clear();
    }

    /**
     * Called when a block is punched.
     * @param bk
     * @param isRightClick
     */
    public void onBlockClick(PlayerInteractEvent evt, Block bk, boolean isRightClick) {

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (shouldHandleEvent(evt.getPlayer()) && evt.hasBlock())
            onBlockClick(evt, evt.getClickedBlock(), evt.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    /**
     * Should we handle an event for the dungeon instance of the given entity?
     * @param e
     * @return shouldHandle
     */
    protected boolean shouldHandleEvent(Entity e) {
        return shouldHandleEvent(e.getWorld());
    }

    /**
     * Should we handle an event that takes place in a given world?
     * @param w
     * @return shouldHandle
     */
    protected boolean shouldHandleEvent(World w) {
        return getDungeon() != null && Dungeons.getDungeon(w) == getDungeon();
    }

    /**
     * Register a TimerTask that will be cancelled upon dungeon exit.
     * @param r
     * @param ticks
     * @return task
     */
    protected BukkitTask addTimerTask(Runnable r, long ticks) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), r, 0, ticks);
        getTasks().add(task);
        return task;
    }

    @PuzzleTrigger
    public void finish(CommandBlock cmd) {
        if (cmd.getLocation().distance(getGateLocation()) <= 10)
            complete();
    }

    /**
     * Execute a trigger for this puzzle.
     *
     * Valid Method Signatures:
     * doStuff()
     * doStuff(CommandBlock)
     * @param trigger
     */
    public void fireTrigger(String trigger, CommandBlock block) {
        if (!triggers.containsKey(getClass())) // Get and cache the trigger map.
            addTriggers(getClass());

        Map<String, Method> t = triggers.get(getClass());
        if (!t.containsKey(trigger))
            return; // Is this trigger applicable to this puzzle?

        try {
            Method m = t.get(trigger); // Fire the method associated with the trigger.
            PuzzleTrigger pt = m.getAnnotation(PuzzleTrigger.class);
            if (!pt.skipCheck() && !canTrigger())
                return; // If the trigger conditions aren't met, don't execute.

            if (m.getParameterCount() > 0) {
                m.invoke(this, block);
            } else {
                m.invoke(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to execute puzzle trigger '" + trigger + "' in " + getClass().getSimpleName() + ".");
        }
    }

    private void addTriggers(Class<?> cls) {
        triggers.putIfAbsent(getClass(), new HashMap<>());
        Map<String, Method> map = triggers.get(getClass());
        Stream.of(cls.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(PuzzleTrigger.class)).forEach(m -> map.putIfAbsent(m.getName(), m)); // Add all triggers from this class.
        if (cls.getSuperclass() != null) // Add the triggers from the parent class.
            addTriggers(cls.getSuperclass());
    }

    /**
     * Stop showing the fake block material to players.
     * @param bk
     */
    protected void resetFakeBlock(Block bk) {
        setFakeBlock(bk, null);
    }

    /**
     * Show a fake block to all the players in the dungeon. Respects metadata, and null will reset the type.
     * @param bk
     * @param type
     */
    @SuppressWarnings("deprecation")
    protected void setFakeBlock(Block bk, Material type) {
        getDungeon().getPlayers().forEach(p -> p.sendBlockChange(bk.getLocation(), type != null ? type : bk.getType(), bk.getData()));
    }

    /**
     * Packet-replace all the blocks near a given location.
     * @param loc
     * @param from
     * @param to
     * @param radius
     */
    protected void replaceNear(Location loc, Material from, Material to, int radius) {
        loc = fixLocation(loc.clone());
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block bk = loc.clone().add(x, y, z).getBlock();
                    if (bk.getType() == from)
                        setFakeBlock(bk, to);
                }
            }
        }
    }

    /**
     * Should puzzle triggers fire right now?
     * @return shouldFire
     */
    protected boolean canTrigger() {
        return !isComplete();
    }

    /**
     * Returns the world this puzzle takes place in.
     * @return world
     */
    public World getWorld() {
        return getDungeon() != null ? getDungeon().getWorld() : null;
    }

    /**
     * Get a location marked by a sign.
     * @param name
     * @return location
     */
    protected Block getBlock(String name) {
        return getDungeon().getBlock(name);
    }
}
