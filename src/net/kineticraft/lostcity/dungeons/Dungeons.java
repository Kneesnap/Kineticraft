package net.kineticraft.lostcity.dungeons;

import com.destroystokyo.paper.Title;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.dungeons.commands.*;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.mechanics.ArmorStands;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import net.kineticraft.lostcity.mechanics.system.BuildType;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
@Restrict(BuildType.PRODUCTION)
public class Dungeons extends Mechanic {

    @Getter private static List<Dungeon> dungeons = new ArrayList<>();

    @Override
    public void onEnable() {
        Core.makeFolder("dungeons");
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () ->
            new ArrayList<>(getDungeons()).stream().filter(d -> d.getPlayers().isEmpty()).forEach(Dungeon::remove), 0L, 600L);
    }

    @Override // Remove all dungeons on shutdown.
    public void onDisable() {
        new ArrayList<>(getDungeons()).forEach(Dungeon::remove);
    }

    @Override
    public void onQuit(Player player) {
        if (isDungeon(player))
            getDungeon(player).removePlayer(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMonsterSpawn(CreatureSpawnEvent evt) {
        evt.setCancelled(isDungeon(evt.getLocation()) && evt.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandDPlay(), new CommandPuzzleTrigger(), new CommandInvoke(), new CommandDBoss());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent evt) { // Prevents block destruction.
        evt.setCancelled(isDungeon(evt.getBlock().getWorld()) && !canEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent evt) {
        evt.setCancelled(isDungeon(evt.getBlock().getWorld()) && !canEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent evt) {
        evt.setCancelled(isDungeon(evt.getPlayer()) && !canEdit(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent messing with item frames.
    public void onItemFrame(PlayerInteractEntityEvent evt) {
        evt.setCancelled(evt.getRightClicked() instanceof ItemFrame && isDungeon(evt.getRightClicked()) && !canEdit(evt.getPlayer()));
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

    @EventHandler
    public void onEnter(PlayerMoveEvent evt) {
        String to = Utils.getRegion(evt.getTo());
        if (!to.equals(Utils.getRegion(evt.getFrom())) && to.startsWith("dungeon_"))
             Dungeons.startDungeon(evt.getPlayer(), DungeonType.valueOf(to.substring("dungeon_".length()).toUpperCase()), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttemptExit(PlayerTeleportEvent evt) {
        Player p = evt.getPlayer();
        if (isDungeon(evt.getFrom()) && !isDungeon(evt.getTo()) && p.getGameMode() != GameMode.SPECTATOR)
            getDungeon(p).alert(p.getName() + " has left the dungeon.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Handle dungeon death.
    public void onPlayerDeath(PlayerDeathEvent evt) {
        if (!isDungeon(evt.getEntity()))
            return;

        evt.setKeepInventory(true); // Don't lose items in a dungeon.
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
        ArmorStand as = ArmorStands.spawnArmorStand(p.getLocation().subtract(0, 0.75, 0), "corpse");
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

        if (ServerUtils.getTicksToReboot() <= 20 * 60 * 30 && !Utils.isStaff(player)) {
            player.sendMessage(ChatColor.RED + "The server is rebooting in less than 30 minutes, dungeons may not be started now.");
            return;
        }

        List<Player> players = Utils.getNearbyPlayers(player, 7);
        players.add(player);
        new ArrayList<>(players).stream().filter(p -> !type.hasUnlocked(player)).forEach(players::remove); // Prevent people without permission.

        if (edit) // Only let staff into edit mode.
            players = players.stream().filter(p -> Utils.getRank(p).isStaff()).collect(Collectors.toList());

        Dungeon dungeon = type.getConstruct().apply(players);
        dungeon.setEditMode(edit);
        dungeon.setup(type);
        getDungeons().add(dungeon);
    }

    /**
     * Can a player make changes to this dungeon?
     * @param player
     * @return canEdit
     */
    private static boolean canEdit(HumanEntity player) {
        return Utils.isStaff(player) && player.getGameMode() == GameMode.CREATIVE;
    }
}
