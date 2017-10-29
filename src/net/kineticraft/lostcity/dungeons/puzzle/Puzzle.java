package net.kineticraft.lostcity.dungeons.puzzle;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.ActionSign;
import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.Dungeons;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
 * Created by Kneesnap on 7/29/2017.
 */
@Getter
public abstract class Puzzle implements Listener {
    private boolean complete;
    private List<BukkitTask> tasks = new ArrayList<>();
    private Map<Block, Material> fakeBlocks = new HashMap<>();
    private Dungeon dungeon;
    private HandlerList[] handlers = new HandlerList[0];

    private static final Map<Class<? extends Puzzle>, Map<String, Method>> triggers = new HashMap<>();

    public Puzzle() {
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
    }

    public Puzzle(HandlerList... events) {
        this();
        handlers = events;
    }


    /**
     * Set the dungeon this puzzle is active in.
     * @param d
     */
    public void setDungeon(Dungeon d) {
        this.dungeon = d;
        onInit();
    }

    /**
     * Get the location of the gate that opens when this puzzle is completed.
     * @return gateLocation.
     */
    public Location getGateLocation() {
        ActionSign bk = getMarker("pz" + (getDungeon().getPuzzles().indexOf(this) + 1));
        return bk != null ? bk.getSign().getLocation() : null;
    }

    /**
     * Get the direction the player should face during the completion cutscene for this puzzle
     * @return gateFace
     */
    public BlockFace getGateFace() {
        return Utils.getDirection(getGateLocation().getBlock());
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
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::onComplete, 20L);

            boolean hasGate = getGateLocation() != null;
            if (!getDungeon().playCutscene("_complete") && hasGate) // Play the default cutscene ending if there is a gate and no custom cutscene.
                getDungeon().playCutscene(new PuzzleDoorCutscene(getGateLocation(), getGateFace()));

            if (hasGate) // Set the gate marker to a redstone block, if it exists.
                getGateLocation().getBlock().setType(Material.REDSTONE_BLOCK);
        }, 35L);
    }

    /**
     * Called when this puzzle is initialized, players have just been teleported into the dungeon.
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
        for (HandlerList hl : getHandlers())
            hl.unregister(this);
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

    /**
     * Called when an entity attacks another in the dungeon.
     * @param attacker
     * @param defender
     * @return Should the attack be considered valid?
     */
    public boolean onEntityAttack(Player attacker, Entity defender) {
       return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (!shouldHandleEvent(evt.getPlayer()) || !evt.hasBlock())
            return;

        Block bk = evt.getClickedBlock();
        onBlockClick(evt, bk, evt.getAction() == Action.RIGHT_CLICK_BLOCK);
        if (getFakeBlocks().containsKey(bk)) { // Make a block's fake material not fail.
            evt.setCancelled(true);
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> setFakeBlock(bk, getFakeBlocks().get(bk)));
        }
    }

    /**
     * Update all fake blocks in the dungeon whose illusions may not be solid.
     */
    public void updateFakeBlocks() {
        getFakeBlocks().forEach(this::setFakeBlock);
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
        if (getGateLocation() == null || cmd.getLocation().distance(getGateLocation()) <= 10)
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
        if (type != null) {
            getFakeBlocks().put(bk, type);
        } else {
            getFakeBlocks().remove(bk);
        }
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
     * Get a sign marker by its type.
     * @param name
     * @return location
     */
    protected ActionSign getMarker(String name) {
        return getDungeon().getSign(name);
    }

    /**
     * Get all action signs with a given type.
     * @param type
     * @return signs
     */
    protected List<ActionSign> getSigns(String type) {
        return getDungeon().getSigns(type);
    }

    /**
     * Is an event that happens happening in this puzzle's world?
     * @param w
     * @return isPuzzle
     */
    protected boolean isPuzzle(World w) {
        return getWorld().equals(w);
    }

    /**
     * Is an entity in this puzzle's world?
     * @param ent
     * @return isPuzzle
     */
    protected boolean isPuzzle(Entity ent) {
        return isPuzzle(ent.getWorld());
    }
}
