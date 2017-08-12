package net.kineticraft.lostcity.dungeons;

import com.destroystokyo.paper.Title;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.cutscenes.Cutscene;
import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.utils.TextBuilder;
import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.utils.ZipUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A base for a dungeon.
 * Created by Kneesnap on 7/11/2017.
 */
@Getter
public class Dungeon {

    private DungeonType type;
    private World world;
    private List<Player> originalPlayers;
    private List<Puzzle> puzzles = new ArrayList<>();
    private Map<String, Block> locations = new HashMap<>();
    @Setter private boolean editMode;

    public Dungeon(List<Player> players) {
        this.originalPlayers = new ArrayList<>(players);
    }

    /**
     * Setup the dungeon files.
     */
    public void setup(DungeonType type) {
        assert getType() == null; // Make sure we haven't setup yet.
        this.type = type;

        getOriginalPlayers().forEach(p ->
                p.sendMessage(ChatColor.GRAY + "Loading Dungeon: '" + getType().getDisplayName() + ChatColor.GRAY + "' -- Please wait..."));

        String worldName = "DUNGEON_" + System.currentTimeMillis() + File.separator;
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            Core.logInfo("Loading dungeon " + getType().name() + " as " + worldName + ".");
            ZipUtil.unzip(getType().getWorld(), worldName);
            Utils.removeFile(worldName + "uid.dat");
            Utils.removeFile(worldName + "players");
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> initWorld(worldName));
        });
    }

    /**
     * Initialize the world, and start the dungeon.
     * @param worldName
     */
    private void initWorld(String worldName) {
        WorldCreator creator = new WorldCreator(worldName);
        creator.generateStructures(false);
        this.world = Bukkit.getServer().createWorld(creator);
        getWorld().setAutoSave(false);
        getWorld().setDifficulty(Difficulty.EASY);
        getWorld().setTime(15000); // Set it to night.

        updateLocations();
        getOriginalPlayers().forEach(p -> {
            Location l = getWorld().getSpawnLocation();
            l.setYaw(90);
            Utils.safeTp(p, l);
            p.setFallDistance(0);

            onJoin(p);
            p.sendTitle(new Title(new TextBuilder(getType().getName()).color(getType().getColor()).create(),
                    new TextBuilder("Objective: ").color(ChatColor.GRAY).append(getType().getEntryMessage()).create(),
                    20, 6, 20));

            if (isEditMode()) {
                p.sendMessage(ChatColor.GREEN + "This dungeon is in EDIT MODE.");
                p.sendMessage(ChatColor.GRAY + " -- Any changes you make will be saved upon exit. Be careful.");
            }
        });

        getPuzzles().forEach(p -> p.setDungeon(this));
    }

    /**
     * Reload the locations saved on signs.
     */
    public void updateLocations() {
        getLocations().clear();
        Arrays.asList(getWorld().getLoadedChunks()).forEach(c -> Arrays.stream(c.getTileEntities())
                .filter(te -> te instanceof Sign).map(te -> (Sign) te)
                .filter(s -> s.getLine(0).startsWith("[") && s.getLine(0).endsWith("]"))
                .forEach(s -> getLocations().put(s.getLine(0).substring(1, s.getLine(0).length() - 1), s.getBlock())));
    }

    /**
     * Get a sign location with the given id.
     * @param name
     * @return location
     */
    public Block getBlock(String name) {
        return getLocations().get(name);
    }

    /**
     * Announce a message to everyone in the dungeon.
     * @param message
     */
    public void announce(String message) {
        getPlayers().forEach(p -> p.sendMessage(message));
    }

    /**
     * Send a dungeon message to all players in the dungeon.
     * @param message
     */
    public void alert(String message) {
        announce(ChatColor.LIGHT_PURPLE + "[Dungeon] " + ChatColor.GRAY + message);
    }

    /**
     * Get a list of players actively attempting the dungeon.
     * @return players
     */
    public List<Player> getPlayers() {
        return getWorld().getPlayers();
    }

    /**
     * Get a list of players in survival mode in the dungeon.
     * @return survivalPlayers
     */
    public List<Player> getSurvivalPlayers() {
        return getPlayers().stream().filter(p -> p.getGameMode() != GameMode.SPECTATOR).collect(Collectors.toList());
    }

    /**
     * Remove this dungeon if there are no players left.
     */
    public void tryRemove() {
        if (getPlayers().isEmpty())
            remove();
    }

    /**
     * Remove this dungeon.
     */
    public void remove() {
        if (getWorld() == null)
            return; // Don't unload dungeon twice.

        // Remove players and unload puzzles.
        Core.logInfo("Removing dungeon " + getWorld().getName() + ".");
        removePlayers();
        getPuzzles().forEach(Puzzle::onDungeonRemove);

        // Handle saving.
        if (isEditMode()) {
            getWorld().getEntities().stream().filter(e -> e instanceof Monster || e instanceof Item).forEach(Entity::remove);
            Stream.of(getWorld().getLoadedChunks()).forEach(Chunk::unload);
            getWorld().save(); // Save data such as Spawn Location
        }

        Bukkit.unloadWorld(getWorld(), isEditMode()); // Unload the world.
        if (isEditMode()) { // Zip up and save the world.
            Core.alertStaff("Saving modified " + getType().name() + " dungeon.");
            ZipUtil.zip(getWorld().getWorldFolder(), getType().getWorld().getPath());
        }

        Utils.removeFile(getWorld().getName()); // Delete the world folder.
        Utils.removeFile("plugins/WorldGuard/worlds/" + getWorld().getName()); // Delete WorldGuard residue.
        Dungeons.getDungeons().remove(this); // Remove this dungeon.
        this.world = null; // Mark this dungeon as unloaded.
    }

    /**
     * Called when this dungeon has been completed.
     */
    public void complete() {
        FireworkEffect.Builder b = FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.BALL);
        getPlayers().forEach(p -> Utils.spawnFirework(p.getLocation(), b)); // Spawn fireworks.

        Title t = new Title(new TextBuilder("Dungeon Complete!").color(ChatColor.GRAY).create(),
                new TextBuilder(getType().getName()).color(getType().getColor()).create());
        getPlayers().forEach(p -> p.sendTitle(t)); // Send titles.

        Core.broadcast(ChatColor.GOLD + getType().getFinishMessage() + " by a group of players."); // Announce victory
        Core.broadcast(ChatColor.GRAY + "Group: " + ChatColor.UNDERLINE
                + getPlayers().stream().map(Entity::getName).collect(Collectors.joining(", ")));
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::giveItem, 200L);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::removePlayers, 400L); // Teleport players out.
    }

    /**
     * Give the collector's item to everyone who completed the dungeon fully.
     */
    protected void giveItem() {
        ItemStack i = Configs.getMainConfig().getDungeonRewards().getValue(getType());
        if (Utils.isAir(i))
            return;

        for (Player p : getPlayers()) {
            if (getOriginalPlayers().contains(p)) {
                Utils.giveItem(p, i.clone());
            } else {
                p.sendMessage(ChatColor.RED + "You teleported into this dungeon mid-session, and therefore did not receive the reward.");
            }
        }
    }

    /**
     * Called when a player joins this dungeon.
     * @param player
     */
    public void onJoin(Player player) {
        alert(player.getName() + " joined the dungeon.");
        Utils.giveItem(player, new ItemStack(Material.STONE_SWORD));
    }

    /**
     * Teleport all players out of the dungeon.
     */
    public void removePlayers() {
        getPlayers().forEach(Utils::toSpawn);
    }

    /**
     * Remove a player from this dungeon, and announce it.
     * @param player
     */
    public void removePlayer(Player player) {
        Utils.toSpawn(player);
        onLeave(player);
    }

    /**
     * Calls when a player leaves the dungeon.
     * @param player
     */
    public void onLeave(Player player) {
        alert(player.getName() + " has left the dungeon.");
        NoteBlockPlayerMain.stopPlaying(player);
        getOriginalPlayers().remove(player);
    }

    /**
     * Register the puzzles in this dungeon.
     * @param puzzles
     */
    protected void registerPuzzles(Puzzle... puzzles) {
        getPuzzles().addAll(Arrays.asList(puzzles));
    }

    /**
     * Fire a trigger for all of the puzzles in this dungeon.
     * @param trigger
     * @param block
     */
    public void triggerPuzzles(String trigger, CommandBlock block) {
        getPuzzles().forEach(p -> p.fireTrigger(trigger, block));
    }

    /**
     * Clone a location and fix it so the world is correct.
     * @param loc
     * @return fixed
     */
    public Location fixLocation(Location loc) {
        if (loc == null)
            return null;
        Location fixed = loc.clone();
        fixed.setWorld(getWorld());
        return fixed;
    }

    /**
     * Return an asset name shared with each dungeon as "d[dungeon_number]_name"
     * @param name
     * @return instance
     */
    public String getInstance(String name) {
        return "d" + (getType().ordinal() + 1) + "_" + name;
    }

    /**
     * Spawn a dungeon boss.
     * @param type
     */
    public void spawnBoss(BossType type) {
        type.spawnBoss(this);
    }

    /**
     * Play a cutscene for all players in the dungeon.
     * @param cutscene
     */
    public void playCutscene(String cutscene) {
        playCutscene(Cutscenes.getCutscenes().get(getInstance(cutscene)));
    }

    /**
     * Play a cutscene for all players in the dungeon.
     * @param c
     */
    public void playCutscene(Cutscene c) {
        c.play(getSurvivalPlayers());
    }

    /**
     * Play a sound to all players in a dungeon.
     * @param sound
     * @param pitch
     */
    public void playSound(Sound sound, float pitch) {
        getPlayers().forEach(p -> p.playSound(p.getLocation(), sound, 1F, pitch));
    }
}