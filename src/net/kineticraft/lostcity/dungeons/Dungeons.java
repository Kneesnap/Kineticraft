package net.kineticraft.lostcity.dungeons;

import com.destroystokyo.paper.Title;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.commands.*;
import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.events.PlayerChangeRegionEvent;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.mechanics.ArmorStands;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Runs the Dungeons of the game.
 * Created by Kneesnap on 7/11/2017.
 */
public class Dungeons extends Mechanic {

    @Getter private static List<Dungeon> dungeons = new ArrayList<>();

    @Override
    public void onEnable() {
        Core.makeFolder("dungeons");
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> new ArrayList<>(getDungeons()).forEach(d -> {
                d.getPuzzles().forEach(Puzzle::updateFakeBlocks); // Update fake blocks
                d.tryRemove(); // Remove this dungeon if there's nobody left.
            }), 0L, 300L);
    }

    @Override
    public void onQuit(Player player) {
        if (isDungeon(player))
            getDungeon(player).removePlayer(player);
    }

    @Override // Remove all dungeons on shutdown.
    public void onDisable() {
        new ArrayList<>(getDungeons()).forEach(Dungeon::remove);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMonsterSpawn(CreatureSpawnEvent evt) {
        evt.setCancelled(isDungeon(evt.getLocation()) && evt.getSpawnReason() != SpawnReason.CUSTOM && evt.getSpawnReason() != SpawnReason.DEFAULT);
    }

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandDPlay(), new CommandPuzzleTrigger(), new CommandInvoke(), new CommandDBoss(), new CommandDMusic());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent evt) {
        evt.setCancelled(preventEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent evt) { // Prevents block destruction.
        evt.setCancelled(preventEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent evt) {
        evt.setCancelled(preventEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent opening inventories bound to blocks.
    public void onInventoryOpen(InventoryOpenEvent evt) {
        evt.setCancelled(evt.getInventory().getHolder() != null && preventEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent messing with item frames.
    public void onItemFrame(PlayerInteractEntityEvent evt) {
        evt.setCancelled(evt.getRightClicked() instanceof ItemFrame && preventEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent hitting item frames.
    public void onItemFrameAttack(EntityDamageByEntityEvent evt) {
        evt.setCancelled(evt.getEntity() instanceof ItemFrame && preventEdit(evt.getDamager()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent rain and snow in dungeons.
    public void onWeather(WeatherChangeEvent evt) {
        evt.setCancelled(isDungeon(evt.getWorld()) && evt.toWeatherState());
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent evt) {
        if (isDungeon(evt.getEntity()))
            evt.setFoodLevel(20);
    }

    @EventHandler(ignoreCancelled = true) // Handles special items entering hoppers.
    public void onHopperPickup(InventoryPickupItemEvent evt) {
        if (!(evt.getInventory().getHolder() instanceof Hopper))
            return; // Verify the inventory the item is going to enter is a hopper.

        Hopper hp = (Hopper) evt.getInventory().getHolder();
        Matcher mName = Pattern.compile("<Custom ID: (\\w+)>").matcher(hp.getInventory().getName());
        if (!mName.find())
            return; // If it doesn't have a Custom item ID defined, don't handle it.

        ItemWrapper iw = ItemManager.constructItem(evt.getItem().getItemStack());
        evt.setCancelled(true);

        if (mName.group(1).equalsIgnoreCase(iw.getTagString("id"))) { // We've found the right item! Consume it.
            evt.getItem().remove();
            hp.getBlock().getRelative(BlockFace.DOWN).setType(Material.REDSTONE_BLOCK);
        } else { // This item isn't acceptable, spit it back out.
            evt.getItem().setVelocity(new Vector(0, .15F, 0));
        }
    }

    @EventHandler // Invoke a dungeon when a player enters a WorldGuard region.
    public void onEnter(PlayerChangeRegionEvent evt) {
        if (!evt.getRegionTo().startsWith("dungeon_"))
            return;
        int dungeonId = Integer.parseInt(evt.getRegionTo().split("_")[1]);
        DungeonType type = dungeonId <= DungeonType.values().length ? DungeonType.values()[dungeonId - 1] : null;
        Dungeons.startDungeon(evt.getPlayer(), type, DungeonUsage.PLAY);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDungeonChange(PlayerTeleportEvent evt) {
        if (!isSameDungeon(evt.getFrom(), evt.getTo())) {
            if (isDungeon(evt.getFrom()))
                getDungeon(evt.getFrom()).onLeave(evt.getPlayer());
            if (isDungeon(evt.getTo())) {
                if (!canEnterDungeon(evt.getPlayer(), getDungeon(evt.getTo()))) {
                    evt.setCancelled(true);
                    return;
                }

                getDungeon(evt.getTo()).onJoin(evt.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Handle dungeon death.
    public void onPlayerDeath(PlayerDeathEvent evt) {
        if (!isDungeon(evt.getEntity()))
            return;

        evt.setKeepLevel(true); // Keep XP, but drop inventory. (Since you can't bring anything in)
        Player p = evt.getEntity();
        makeCorpse(p);
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // Restore to max health.
        p.setGameMode(GameMode.SPECTATOR); // Set to spectator.
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 2)); // Give blindness.
        p.sendTitle(new Title(ChatColor.RED + "Dungeon Failed"));
        getDungeon(p).alert(p.getName() + " has been eliminated.");
        Utils.stopNBS(p); //Disable music.
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            Utils.toSpawn(p); // Teleport to spawn.
            p.setGameMode(GameMode.SURVIVAL);
        }, 50L);
    }

    /**
     * Create an armor stand corpse for a dead player.
     * @param p
     */
    public static void makeCorpse(Player p) {
        Location spawn = Utils.findSafe(p.getLocation()).subtract(0, 1.2, 0);
        ArmorStand as = ArmorStands.spawnArmorStand(spawn, "corpse");
        as.setGravity(false);
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
     * Confirms l1 is a dungeon and both of the dungeons match.
     * @param l1
     * @param l2
     * @return dungeon
     */
    public static boolean isSameDungeon(Location l1, Location l2) {
        return (isDungeon(l1) || isDungeon(l2)) && getDungeon(l1) == getDungeon(l2);
    }

    /**
     * Get the dungeon housed in the given world. Null if not a dungeon.
     * @param world
     * @return dungeon
     */
    public static Dungeon getDungeon(World world) {
        return world != null ? getDungeons().stream().filter(d -> world.equals(d.getWorld())).findFirst().orElse(null) : null;
    }

    /**
     * Get a dungeon by the world of a supplied location.
     * @param location
     * @return dungeon
     */
    public static Dungeon getDungeon(Location location) {
        return getDungeon(location.getWorld());
    }

    /**
     * Get the dungeon that a given block is placed in.
     * @param block
     * @return dungeon
     */
    public static Dungeon getDungeon(Block block) {
        return getDungeon(block.getWorld());
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
     * Check if a player has an empty inventory.
     * @param p
     * @return isEmpty
     */
    private static boolean isInventoryEmpty(Player p) {
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            if (!Utils.isAir(p.getInventory().getItem(i))) {
                p.sendMessage(ChatColor.RED + "Your inventory must be empty to enter a dungeon.");
                return false;
            }
        }
        return true;
    }

    /**
     * Can a player enter a dungeon already in progress?
     * @param player
     * @param dungeon
     * @return canEnterDungeon
     */
    private static boolean canEnterDungeon(Player player, Dungeon dungeon) {
        if (!Utils.isStaff(player)) {
            if (dungeon.hasFinalBossSpawned()) {
                player.sendMessage(ChatColor.RED + "You may not enter a dungeon after the final boss spawns.");
                return false;
            }
        }

        return canEnterDungeon(player, dungeon.getType());
    }

    /**
     * Return if a player can enter a dungeon.
     * @param player
     * @param type
     * @return canEnter
     */
    private static boolean canEnterDungeon(Player player, DungeonType type) {
        return Utils.isStaff(player) || (isInventoryEmpty(player) && type.hasUnlocked(player));
    }

    /**
     * Start a dungeon for the given player.
     * @param player
     * @param type
     * @param usage
     */
    public static void startDungeon(Player player, DungeonType type, DungeonUsage usage) {
        if (type == null || !type.isReleased()) {
            player.sendMessage(ChatColor.RED + "This dungeon has not released yet.");
            return;
        }

        if (!canEnterDungeon(player, type) || MetadataManager.updateCooldownSilently(player, "dungeon", 50))
            return;

        if (usage == DungeonUsage.PLAY) {
            if (ServerUtils.getTicksToReboot() <= 20 * 60 * 30) {
                player.sendMessage(ChatColor.RED + "The server is rebooting in less than 30 minutes, dungeons may not be started now.");
                return;
            }

            if (getDungeons().size() >= 5) {
                player.sendMessage(ChatColor.RED + "The maximum number of dungeons are open. Try again later.");
                return;
            }
        }

        List<Player> players = new ArrayList<>();
        players.add(player);
        if (usage == DungeonUsage.PLAY) {
            players.addAll(Utils.getNearbyPlayers(player, 7));
            new ArrayList<>(players).stream().filter(p -> !canEnterDungeon(p, type)).forEach(players::remove); // Prevent people without permission.
        }

        Dungeon dungeon = type.getConstruct().apply(players);
        dungeon.setEditMode(usage == DungeonUsage.EDIT);
        dungeon.setup(type);
        getDungeons().add(dungeon);
    }

    /**
     * Can a player make changes to this dungeon?
     * @param player
     * @return canEdit
     */
    private static boolean canEdit(Entity player) {
        return Utils.isStaff(player)
                && (!(player instanceof HumanEntity) || ((HumanEntity) player).getGameMode() != GameMode.SURVIVAL);
    }

    /**
     * Return if we should block an entity making changes to a world.
     * @param ent
     * @return shouldPrevent
     */
    private static boolean preventEdit(Entity ent) {
        return isDungeon(ent) && !canEdit(ent);
    }
}
