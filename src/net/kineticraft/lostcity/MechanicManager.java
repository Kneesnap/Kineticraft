package net.kineticraft.lostcity;

import lombok.Getter;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.crake.Crake;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.guis.GUIManager;
import net.kineticraft.lostcity.item.Items;
import net.kineticraft.lostcity.mechanics.*;
import net.kineticraft.lostcity.mechanics.enchants.Enchants;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * MechanicManager - Handles main mechanic methods.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class MechanicManager implements Listener {

    @Getter
    private static List<Mechanic> mechanics = new ArrayList<>();

    /**
     * Registers all game mechanics.
     */
    public static void registerMechanics() {
        Core.logInfo("Registering Mechanics...");
        Bukkit.getPluginManager().registerEvents(new MechanicManager(), Core.getInstance()); // Ready to listen for event.

        // Register all mechanics here, in order of startup:
        registerMechanic(new Configs());
        registerMechanic(new DataHandler());
        registerMechanic(new Punishments());
        registerMechanic(new Callbacks());
        registerMechanic(new Commands());
        registerMechanic(new Crake());
        registerMechanic(new DiscordAPI());
        registerMechanic(new Restrictions());
        registerMechanic(new ServerManager());
        registerMechanic(new GeneralMechanics());
        registerMechanic(new Vanish());
        registerMechanic(new GUIManager());
        registerMechanic(new SleepMechanics());
        registerMechanic(new SlimeFinder());
        registerMechanic(new Items());
        registerMechanic(new Chat());
        registerMechanic(new CompassMechanics());
        registerMechanic(new FarmLimiter());
        registerMechanic(new Leashes());
        registerMechanic(new Enchants());
        registerMechanic(new Voting());
        registerMechanic(new AFK());
        registerMechanic(new MetadataManager());
        Core.logInfo("Mechanics Registered.");
    }

    /**
     * Register a single game mechanic.
     */
    private static void registerMechanic(Mechanic m) {
        getMechanics().add(m);
        m.onEnable();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() != Core.getInstance()) // KCv4 isn't unloading.
            return;
        Core.logInfo("Shutting down...");
        getMechanics().forEach(Mechanic::onDisable); // Unload all mechanics.
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        evt.setJoinMessage(null);
        announceStatus(evt.getPlayer(), "joined");
        getMechanics().forEach(m -> m.onJoin(evt.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        evt.setQuitMessage(null);
        onLeave(evt.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        evt.setLeaveMessage(null);
        onLeave(evt.getPlayer());
    }

    /**
     * Handles a player disconnecting.
     * @param player
     */
    private void onLeave(Player player) {
        announceStatus(player, "left");
        getMechanics().forEach(m -> m.onQuit(player)); // Tell each mechanic they're leaving.
    }

    private void announceStatus(Player player, String action) {
        KCPlayer kc = KCPlayer.getWrapper(player);
        if (kc.isVanished())
            return;

        Utils.broadcastExcept(ChatColor.YELLOW.toString() + ChatColor.BOLD + " > "
                + (kc.getTemporaryRank().isAtLeast(EnumRank.THETA) ? kc.getTemporaryRank().getNameColor() : ChatColor.YELLOW)
                + kc.getUsername() + ChatColor.YELLOW + " has " + action + ".", player);
    }
}
