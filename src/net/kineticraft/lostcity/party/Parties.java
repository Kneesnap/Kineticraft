package net.kineticraft.lostcity.party;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.party.games.PartyGame;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Handles the wondrous Kineticraft Parties.
 * Created by Kneesnap on 9/14/2017.
 */
public class Parties extends Mechanic {
    @Getter private static World partyWorld;

    @Override
    public void onEnable() {
        partyWorld = new WorldCreator("party_world").seed(1000000).generateStructures(false).createWorld(); // Load party-world.
        if (getParty() != null)
            getParty().setup(); // Automatically setup party on server load.

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (isPartyTime())
                Bukkit.broadcastMessage(ChatColor.AQUA + "There is a party going on! Do " + ChatColor.YELLOW + "/party" + ChatColor.AQUA + " to attend!");
        }, 0L, 6000L);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent evt) {
        if (isParty(evt.getEntity()))
            evt.setFoodLevel(20);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent evt) {
        if (!evt.getTo().getWorld().equals(evt.getFrom().getWorld()) && evt.getFrom().getWorld().equals(getPartyWorld()))
            getPlaying(evt.getPlayer()).forEach(g -> g.removePlayer(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent evt) {
        evt.setCancelled(isParty(evt.getRightClicked()) && !Utils.isStaff(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrame(PlayerInteractEntityEvent evt) {
        evt.setCancelled(isParty(evt.getRightClicked()) && evt.getRightClicked() instanceof ItemFrame && !Utils.isStaff(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onElytraToggle(EntityToggleGlideEvent evt) { // Disables elytra in party world.
        evt.setCancelled(evt.isGliding() && isParty(evt.getEntity()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent evt) {
        evt.setCancelled(playerMove(evt.getPlayer(), evt.getTo()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        evt.setCancelled(playerMove(evt.getPlayer(), evt.getTo()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeafDecay(LeavesDecayEvent evt) {
        evt.setCancelled(isParty(evt.getBlock()));
    }

    @EventHandler(ignoreCancelled = true) // Handles general block-decay. (ie: snow melt)
    public void onBlockDecay(BlockFadeEvent evt) {
        evt.setCancelled(isParty(evt.getBlock()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(EntityBlockFormEvent evt) {
        evt.setCancelled(isParty(evt.getBlock()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent ender pearls in the party world.
    public void onEnderPearl(PlayerTeleportEvent evt) {
        evt.setCancelled((evt.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT  || evt.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
                && isParty(evt.getTo().getWorld()));
    }

    @EventHandler(ignoreCancelled = true) // Block fall damage.
    public void onFallDamage(EntityDamageEvent evt) {
        evt.setCancelled(isParty(evt.getEntity()) && evt.getCause() == EntityDamageEvent.DamageCause.FALL);
    }

    @EventHandler(ignoreCancelled = true) // Prevent players from being damaged in the party world unless in a game.
    public void onPlayerDamage(EntityDamageEvent evt) {
        if (!isParty(evt.getEntity()))
            return;

        if (!(evt.getEntity() instanceof LivingEntity))
            evt.setCancelled(true); // Default to no damage, if the entity isn't alive.

        if (!(evt.getEntity() instanceof Player))
            return; // If the damaged isn't a player, ignore this next bit that may uncancel it.
        Player p = (Player) evt.getEntity();
        evt.setCancelled(!checkParty(p, PartyGame::allowDamage));
        if (evt instanceof EntityDamageByEntityEvent) { // Handle combat differently.
            EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) evt;
            boolean isMob = evt.getEntity() instanceof Creature;
            boolean isPlayer = dmg.getEntity() instanceof Player && dmg.getDamager() instanceof Player;
            evt.setCancelled((!checkParty(p, PartyGame::allowMobCombat) && isMob) || (!checkParty(p, PartyGame::allowPlayerCombat) && isPlayer));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent evt) {
        evt.setCancelled(isParty(evt.getPlayer()) && !Utils.isStaff(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true) // Prevent block placement by non staff.
    public void onBlockPlace(BlockPlaceEvent evt) {
        evt.setCancelled(!Utils.isStaff(evt.getPlayer()) && isParty(evt.getBlock()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent evt) {
        evt.setCancelled(!Utils.isStaff(evt.getPlayer()) && isParty(evt.getBlock()));
    }

    @EventHandler(ignoreCancelled = true) // Prevents natural weather conditions in party world.
    public void onWeather(WeatherChangeEvent evt) {
        evt.setCancelled(isParty(evt.getWorld()) && evt.toWeatherState());
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent evt) {
        Block b = evt.getClickedBlock();
        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK || !b.getWorld().equals(getPartyWorld()) || !b.getType().name().contains("SIGN"))
            return;
        Sign sign = (Sign) b.getState();
        Matcher m = Pattern.compile("§[0-9a-f]\\[(.+)]").matcher(sign.getLine(0));
        if (!m.find())
            return; // The sign isn't applicable :/
        String action = Utils.capitalize(m.group(1));
        Player p = evt.getPlayer();
        PartyGame game = getGames().stream().filter(g -> g.getName().equals(sign.getLine(1))).findAny().orElse(null);

        if (action.equals("Spawn")) { // Player is teleporting to party spawn.
            getParty().teleportIn(p);
        } else if (action.equals("Join") || action.equals("Play")) { // Player is joining a party-game.
            if (game != null) {
                game.addPlayer(p);
            } else {
                p.sendMessage(ChatColor.RED + "Could not find game. Is it enabled?");
            }
        } else if (action.equals("Goto") && game != null && game.getExitLocation() != null) {
            p.teleport(game.getExitLocation());
        } else if (action.equals("Leave") || action.equals("Quit")) { // Player is quitting their current game.
            getPlaying(p).forEach(g -> g.removePlayer(p));
        } else {
            getGames().forEach(g -> g.signAction(action, p, sign));
        }

        if (!sign.getLine(3).contains("Click Me")) { // Automatically add (Click Me!) to signs that don't have it.
            sign.setLine(3, "(Click Me!)");
            sign.update();
        }
    }

    @Override // Teleport players out on quit, prevents logging in at bad coordinates.
    public void onQuit(Player player) {
        if (player.getWorld().equals(getPartyWorld())) {
            getGames().forEach(g -> g.removePlayer(player)); // Player has quit active games.
            Utils.toSpawn(player);
        }
    }

    @Override
    public void onJoin(Player player) {
        if (player.getWorld().equals(getPartyWorld()))
            Utils.toSpawn(player); // Teleport a player to spawn if they spawn in the party world.

        if (isPartyTime())
            Bukkit.getScheduler().runTaskLater(Core.getInstance(),
                    () -> player.sendMessage(ChatColor.AQUA + "There is a party going on! Do " + ChatColor.YELLOW + "/party" + ChatColor.AQUA + " to attend!"), 100);
    }

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandParty());
    }

    @EventHandler @SneakyThrows // Sets the server icon to special party icons.
    public void onPing(ServerListPingEvent evt) {
        if (!isPartyTime())
            return;
        File f = new File("./icons/" + getParty().getName() + ".png");
        if (f.exists())
            evt.setServerIcon(Bukkit.loadServerIcon(f));
    }

    /**
     * Get a list of active games.
     * @return games
     */
    public static List<PartyGame> getGames() {
        return isPartyTime() ? getParty().getGames() : new ArrayList<>();
    }

    /**
     * Is there an active party?
     * @return isPartyTime
     */
    public static boolean isPartyTime() {
        return getParty() != null;
    }

    /**
     * Get the active party, if any.
     * @return activeParty
     */
    public static Party getParty() {
        return Configs.getMainConfig().getParty();
    }

    /**
     * Activate a party.
     * @param p - Active party.
     */
    public static void setParty(Party p) {
        if (p == getParty())
            return; // Don't allow setting to the active party.

        String status = (p != null ? "Kineticraft " + p.getName() + " P" : "p") + "arty has " // Broadcast information to discord.
                + (p != null ? "started! To participate, type /party in-game!" : "ended! Thanks everyone for an awesome party.");
        DiscordAPI.sendMessage(DiscordChannel.ANNOUNCEMENTS, "@everyone The " + status);

        String name = p != null ? p.getName() : getParty().getName(); // Broadcast in-game.
        Core.broadcast(ChatColor.AQUA + "The " + ChatColor.YELLOW + name + " Party" + ChatColor.AQUA + " has " + (p != null ? "started" : "ended") + "!");

        if (p != null) {
            p.setup(); // Setup party.
        } else {
            getPartyWorld().getPlayers().forEach(pl -> getPlaying(pl).forEach(g -> g.removePlayer(pl)));
            getPartyWorld().getPlayers().forEach(Utils::toSpawn); // Teleport all players in the party world out.
        }
        Configs.getMainConfig().setParty(p); // Set the party in the config.
    }

    /**
     * Get a list of games the player is currently playing.
     * @param p
     * @return games
     */
    public static List<PartyGame> getPlaying(Player p) {
        return getGames().stream().filter(g -> g.isPlaying(p)).collect(Collectors.toList());
    }

    private static boolean checkParty(Player p, Predicate<PartyGame> checker) {
        return getPlaying(p).stream().anyMatch(checker);
    }

    /**
     * Handles if a player exits a game arena.
     * @param player - The player moving somewhere
     * @param to - The location the player is going to.
     * @return Should the player movement be cancelled?
     */
    private static boolean playerMove(Player player, Location to) {
        for (PartyGame pg : getGames()) {
            boolean playing = pg.isPlaying(player);
            if (pg.getArena() != null && pg.getArena().contains(to) != playing && (player.getGameMode() != GameMode.SPECTATOR || playing)) {
                player.sendMessage(ChatColor.RED + (pg.removePlayer(player) ? "Due to leaving the arena, you have been removed from the game."
                        : "Please do not try to teleport into this game."));
                return true;
            }
        }
        return false;
    }

    private static boolean isParty(Block b) {
        return isParty(b.getWorld());
    }

    public static boolean isParty(Entity ent) {
        return isParty(ent.getWorld());
    }

    private static boolean isParty(World w) {
        return w != null && w.equals(getPartyWorld());
    }
}
