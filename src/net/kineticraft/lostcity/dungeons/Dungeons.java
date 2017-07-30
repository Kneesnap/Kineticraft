package net.kineticraft.lostcity.dungeons;

import com.destroystokyo.paper.Title;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.dungeons.barleyshope.LazerPuzzle;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.ArmorStands;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Runs the Dungeons of the game.
 * Created by Kneesnap on 7/11/2017.
 */
@Restrict(BuildType.PRODUCTION)
public class Dungeons extends Mechanic {

    @Getter private static List<Dungeon> dungeons = new ArrayList<>();

    @Override
    public void onEnable() {
        Core.makeFolder("dungeons");
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () ->
            new ArrayList<>(getDungeons()).stream().filter(d -> d.getPlayers().isEmpty()).forEach(Dungeon::remove), 0L, 600L);
        new LazerPuzzle(new Location(Core.getMainWorld(), 17, 3, 282));
    }

    @Override // Remove all dungeons on shutdown.
    public void onDisable() {
        getDungeons().forEach(Dungeon::remove);
    }

    @Override
    public void onQuit(Player player) {
        if (isDungeon(player))
            getDungeon(player).removePlayer(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent evt) { // Prevents block destruction.
        evt.setCancelled(isDungeon(evt.getBlock().getWorld()) && !Utils.isStaff(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent evt) {
        evt.setCancelled(isDungeon(evt.getBlock().getWorld()) && !Utils.isStaff(evt.getPlayer()));
    }

    @EventHandler
    public void onEnter(PlayerMoveEvent evt) {
        String to = Utils.getRegion(evt.getTo());
        if (!to.equals(Utils.getRegion(evt.getFrom())) && to.startsWith("dungeon_"))
             Dungeons.startDungeon(evt.getPlayer(), DungeonType.valueOf(to.substring("dungeon_".length()).toUpperCase()), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttemptExit(PlayerTeleportEvent evt) {
        if (!isDungeon(evt.getFrom()) || isDungeon(evt.getTo()))
            return;
        evt.setCancelled(true);
        evt.getPlayer().sendMessage(ChatColor.RED + "You may not teleport out of a dungeon. Use /dquit if you'd like to exit.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Handle dungeon death.
    public void onPlayerDeath(PlayerDeathEvent evt) {
        if (!isDungeon(evt.getEntity()))
            return;

        Player p = evt.getEntity();
        makeCorpse(p);
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // Restore to max health.
        p.setGameMode(GameMode.SPECTATOR); // Set to spectator.
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 2)); // Give blindness.
        p.sendTitle(new Title(ChatColor.RED + "Dungeon Failed"));
        getDungeon(p).announce(ChatColor.LIGHT_PURPLE + "[Dungeon] " + ChatColor.GRAY + p.getName() + " has been eliminated.");
        // TODO: Sound
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            Utils.toSpawn(p);
            p.setGameMode(GameMode.SURVIVAL);
        }, 50L);
    }

    /**
     * Create an armor stand corpse for a dead player.
     * @param p
     */
    private static void makeCorpse(Player p) {
        ArmorStand as = ArmorStands.spawnArmorStand(p.getLocation().subtract(0, 1, 0), "corpse");
        Utils.mirrorItems(p, as);
        as.setHelmet(ItemManager.makeSkull(p.getName()));
        as.setCustomName(ChatColor.RED + p.getName() + "'s Corpse");
        as.setCustomNameVisible(true);
    }

    /**
     * Get all current dungeons that are the supplied dungeon type.
     * @param type
     * @return dungeons
     */
    public static List<Dungeon> getDungeons(DungeonType type) {
        return getDungeons().stream().filter(d -> d.getType() == type).collect(Collectors.toList());
    }

    /**
     * Is this entity in a dungeon?
     * @param entity
     * @return isDungeon
     */
    public static boolean isDungeon(Entity entity) {
        return isDungeon(entity.getWorld());
    }

    /**
     * Is the given location in a dungeon?
     * @param loc
     * @return isDungeon
     */
    public static boolean isDungeon(Location loc) {
        return isDungeon(loc.getWorld());
    }

    /**
     * Is the supplied world a dungeon?
     * @param world
     * @return isDungeon
     */
    public static boolean isDungeon(World world) {
        return getDungeon(world) != null;
    }

    /**
     * Get the dungeon housed in the given world. Null if not a dungeon.
     * @param world
     * @return dungeon
     */
    public static Dungeon getDungeon(World world) {
        return getDungeons().stream().filter(d -> d.getWorld().equals(world)).findFirst().orElse(null);
    }

    /**
     * Get the dungeon that this entity or player is currently in.
     * @param e
     * @return dungeon
     */
    public static Dungeon getDungeon(Entity e) {
        return getDungeon(e.getWorld());
    }

    /**
     * Start a dungeon for the given player.
     * @param player
     * @param type
     * @param edit
     */
    public static void startDungeon(Player player, DungeonType type, boolean edit) {
        if (!type.hasUnlocked(player))
            return;

        if (MetadataManager.updateCooldownSilently(player, "dungeon", 50))
            return;

        if (ServerUtils.getTicksToReboot() <= 20 * 60 * 30 && !Utils.getRank(player).isStaff()) {
            player.sendMessage(ChatColor.RED + "The server is rebooting in less than 30 minutes, dungeons may not be started now.");
            return;
        }

        List<Player> players = new ArrayList<>(Utils.getNearbyPlayers(player, 7));
        players.add(player);
        new ArrayList<>(players).stream().filter(p -> !type.hasUnlocked(player)).forEach(players::remove); // Prevent people without permission.

        if (edit) // Only let staff into edit mode.
            players = players.stream().filter(p -> Utils.getRank(p).isStaff()).collect(Collectors.toList());

        Dungeon dungeon = ReflectionUtil.construct(type.getDungeonClass(), players);
        dungeon.setEditMode(edit);
        dungeon.setup(type);
        getDungeons().add(dungeon);
    }
}
