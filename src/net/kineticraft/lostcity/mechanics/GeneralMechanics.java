package net.kineticraft.lostcity.mechanics;

import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.xxmicloxx.NoteBlockAPI.SongEndEvent;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.events.PlayerChangeRegionEvent;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.TextUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.stream.Collectors;

/**
 * GeneralMechanics - Small general mechanics.
 * Created by Kneesnap on 5/29/2017.
 */
public class GeneralMechanics extends Mechanic {

    private static Objective idObjective;

    @Override
    public void onEnable() {

        // Increment time played.
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () ->
            Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper)
                    .forEach(p -> p.setSecondsPlayed(p.getSecondsPlayed() + 1)), 0L, 20L);

        // Display donor particles.
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            for (Player p : Core.getOnlinePlayers()) {
                KCPlayer w = KCPlayer.getWrapper(p);
                if (w.getEffect() != null && p.getGameMode() != GameMode.SPECTATOR) {
                    double x = Utils.randDouble(0, .5) - .25D;
                    double z = Utils.randDouble(0, .5) - .25D;
                    p.getWorld().spawnParticle(w.getEffect(), p.getEyeLocation().add(0, 1.5, 0), 10, x, -1F, z, .5);
                }
            }
        }, 0L, 20L);

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            if (Bukkit.hasWhitelist() && !ServerUtils.isDevServer())
                return; // Don't deploy while the whitelist is active.

            int newSize = Utils.readSize("patchnotes.txt");
            if (newSize != Configs.getMainConfig().getLastNotesSize()) {
                int newPatch = Configs.getMainConfig().getBuild() + 1;

                Configs.getMainConfig().setBuild(newPatch);
                Configs.getMainConfig().setLastNotesSize(newSize);
                Configs.getMainConfig().saveToDisk();

                DiscordAPI.sendMessage(DiscordChannel.ANNOUNCEMENTS,
                        "@everyone Patch #" + newPatch + " has been deployed.\n\n"
                                + TextUtils.fromMarkup(Utils.readLines("patchnotes.txt").stream()
                                .collect(Collectors.joining("\n"))).toLegacy());
            }
        }, 50L);

        // Don't allow players on top of the nether.
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () ->
                Bukkit.getOnlinePlayers().stream().filter(p -> p.getLocation().getBlockY() >= 127)
                    .filter(p -> p.getWorld().getEnvironment() == World.Environment.NETHER)
                    .forEach(p -> {
                        Location loc = p.getLocation().clone();
                        loc.setY(124);

                        // Make it safe.
                        loc.getBlock().setType(Material.AIR);
                        loc.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
                        p.teleport(loc);
                    }), 0L, 20L);

        idObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("id");
        if (idObjective == null)
            idObjective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("id", "dummy");
        idObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        EnumRank.createTeams(); // Create all the rank teams, in order.
    }

    @EventHandler // Handle repeating songs.
    public void onSongEnd(SongEndEvent evt) {
        if (Utils.getRepeat().contains(evt.getSongPlayer()))
            Utils.repeatNBS(evt.getSongPlayer());
    }

    @EventHandler // Lets staff color signs.
    public void onSignUpdate(SignChangeEvent evt) {
        if (Utils.isStaff(evt.getPlayer()))
            for (int i = 0; i < evt.getLines().length; i++)
                evt.setLine(i, ChatColor.translateAlternateColorCodes('&', evt.getLine(i)));
    }

    @Override
    public void onQuit(Player player) {
        NoteBlockPlayerMain.stopPlaying(player);
    }

    @Override
    public void onJoin(Player player) {
        if (KCPlayer.getWrapper(player).getNotes().join("").length() > 0)
            Core.alertStaff(player.getName() + " has notes. See "  + ChatColor.YELLOW + "/notes " + player.getName()
                    + ChatColor.RED + " for details.");

        String gray = ChatColor.GRAY.toString() + ChatColor.ITALIC;
        player.sendMessage(TextUtils.centerChat(ChatColor.BOLD + "Kineticraft v4"));
        player.sendMessage(TextUtils.centerChat(ChatColor.AQUA.toString() + ChatColor.BOLD + "The Lost City"));
        player.sendMessage(TextUtils.centerChat(gray + "http://kineticraft.net/"));
        player.sendMessage("");
        player.sendMessage(gray + "Type " + ChatColor.YELLOW.toString() + ChatColor.ITALIC
                + "/info" + gray + " for help getting started.");
        player.sendMessage("");

        // 50 tick difference.
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () ->
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, .6929134F), 45L);

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () ->
                player.playSound(player.getLocation(), Sound.ENTITY_HORSE_ARMOR, .85F, 1.480315F), 95L);

        KCPlayer pw = KCPlayer.getWrapper(player);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            int newBuild = Configs.getMainConfig().getBuild();
            if (pw.isOnline() && pw.getLastBuild() != newBuild) {
                TextUtils.sendMarkup(player, "&b  ❢  &aBuild #%s &7patch notes now available. "
                        + "[command=/patchnotes]&e&l&nVIEW[/command][hover]Click here to view the latest changes.[/hover]"
                        + "  &b❢", newBuild);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F);
            }
        }, 125L);

        idObjective.getScore(player.getName()).setScore(KCPlayer.getWrapper(player).getAccountId());
    }

    @EventHandler // Prevent players from renaming villagers in spawn.
    public void onNameTagUsage(PlayerInteractEntityEvent evt) {
        if (evt.getPlayer().getInventory().getItem(evt.getHand()).getType() == Material.NAME_TAG
                && Utils.inSpawn(evt.getRightClicked().getLocation())) {
            evt.setCancelled(true);
            evt.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        if (evt.getPlayer().hasPlayedBefore())
            return;
        Utils.giveItem(evt.getPlayer(), ItemManager.makeClaimShovel());
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> evt.getPlayer().teleport(Core.getMainWorld().getSpawnLocation()));
        Utils.getAllPlayersExcept(evt.getPlayer()).forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1.1F));
        Core.broadcast(ChatColor.GRAY + "Welcome " + ChatColor.GREEN + evt.getPlayer().getName()
                + ChatColor.GRAY + " to " + ChatColor.BOLD + "Kineticraft" + ChatColor.GRAY + "!");
    }

    @EventHandler(ignoreCancelled = true)
    public void onEggPunch(PlayerInteractEvent evt) {
        Block block = evt.getClickedBlock();
        if ((evt.getAction() != Action.LEFT_CLICK_BLOCK && evt.getAction() != Action.RIGHT_CLICK_BLOCK)
                || block == null || block.getType() != Material.DRAGON_EGG)
            return;

        evt.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You have picked up the egg.");
        block.setType(Material.AIR);
        Utils.giveItem(evt.getPlayer(), new ItemStack(Material.DRAGON_EGG));
        evt.setCancelled(true);
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent evt) {
        if (evt.getEntityType() != EntityType.ENDER_DRAGON)
            return; // If it's not a dragon, ignore it.
        evt.setDroppedExp(4000);
        Block bk = evt.getEntity().getWorld().getBlockAt(0, 75, 0);

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            bk.setType(Material.DRAGON_EGG);
            bk.getWorld().getNearbyEntities(bk.getLocation(), 50, 50, 50)
                    .forEach(e -> e.sendMessage(ChatColor.GRAY + "As the dragon dies, an egg forms below."));
        }, 15 * 20);
    }

    @EventHandler // Water-vision.
    public void onWaterTraverse(PlayerMoveEvent evt) {
        Utils.setPotion(evt.getPlayer(), PotionEffectType.WATER_BREATHING, evt.getPlayer().getVehicle() == null
                && Utils.inSpawn(evt.getPlayer().getLocation()) && evt.getTo().clone().add(0, 1, 0).getBlock().isLiquid());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLightningStrike(LightningStrikeEvent evt) {
        evt.setCancelled(Utils.nextBool());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent evt) {
        if ((!evt.getTo().getWorld().equals(evt.getFrom().getWorld()) || evt.getTo().distance(evt.getFrom()) >= 10) && Utils.isStaff(evt.getPlayer()))
            KCPlayer.getPlayer(evt.getPlayer()).setLastLocation(evt.getFrom());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent evt) {
        PlayerChangeRegionEvent change = new PlayerChangeRegionEvent(evt);
        if (!change.getRegionFrom().equals(change.getRegionTo()))
           Bukkit.getPluginManager().callEvent(change);
    }
}