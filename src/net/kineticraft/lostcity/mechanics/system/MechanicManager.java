package net.kineticraft.lostcity.mechanics.system;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.crake.Crake;
import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.dungeons.Dungeons;
import net.kineticraft.lostcity.entity.Entities;
import net.kineticraft.lostcity.events.MechanicRegisterEvent;
import net.kineticraft.lostcity.guis.GUIManager;
import net.kineticraft.lostcity.item.Items;
import net.kineticraft.lostcity.mechanics.*;
import net.kineticraft.lostcity.mechanics.enchants.Enchants;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.party.Parties;
import net.kineticraft.lostcity.utils.ReflectionUtil;
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

    @Getter private static List<Mechanic> mechanics = new ArrayList<>();

    /**
     * Register built-in mechanics.
     */
    private static void registerDefault() {
        addMechanic(Configs.class);
        addMechanic(DataHandler.class);
        addMechanic(Punishments.class);
        addMechanic(Callbacks.class);
        addMechanic(Toggles.class);
        addMechanic(Cutscenes.class);
        addMechanic(Commands.class);
        addMechanic(Crake.class);
        addMechanic(DiscordAPI.class);
        addMechanic(Restrictions.class);
        addMechanic(ServerManager.class);
        addMechanic(GeneralMechanics.class);
        addMechanic(Vanish.class);
        addMechanic(GUIManager.class);
        addMechanic(SleepMechanics.class);
        addMechanic(SlimeFinder.class);
        addMechanic(Items.class);
        addMechanic(Chat.class);
        addMechanic(CompassMechanics.class);
        addMechanic(Entities.class);
        addMechanic(Dungeons.class);
        addMechanic(FarmLimiter.class);
        addMechanic(ArmorStands.class);
        addMechanic(Leashes.class);
        addMechanic(Enchants.class);
        addMechanic(Voting.class);
        addMechanic(Parties.class);
        addMechanic(AFK.class);
        addMechanic(MetadataManager.class);
    }

    /**
     * Get a mechanic's instance from its class.
     * @param mechanicClass
     * @param <T>
     * @return mechanicInstance
     */
    @SuppressWarnings("unchecked")
    public static <T extends Mechanic> T getInstance(Class<T> mechanicClass) {
        return (T) getMechanics().stream().filter(m -> m.getClass().equals(mechanicClass)).findAny().orElse(null);
    }

    /**
     * Register a game mechanic class, if it can be registered on this build.
     * @param mechanicClass
     */
    public static void addMechanic(Class<? extends Mechanic> mechanicClass) {
        addMechanic(ReflectionUtil.construct(mechanicClass));
    }

    /**
     * Register a game mechanic, if it can be registered on this build.
     * @param m
     */
    public static void addMechanic(Mechanic m) {
        Utils.confirm(getInstance(m.getClass()) == null, m.getClass().getSimpleName() + " is already registered!");
        if (!Core.isApplicableBuild(m))
            return;
        MechanicManager.getMechanics().add(m);
        Bukkit.getPluginManager().registerEvents(m, Core.getInstance());
    }

    /**
     * Registers all game mechanics.
     */
    public static void registerMechanics() {
        Core.logInfo("Registering Mechanics...");
        Bukkit.getPluginManager().registerEvents(new MechanicManager(), Core.getInstance()); // Ready to listen for event.

        // Register all mechanics here, in order of startup:
        registerDefault();
        Bukkit.getPluginManager().callEvent(new MechanicRegisterEvent()); // Tell other plugins to register their mechanics.
        getMechanics().forEach(Mechanic::onEnable);
        Core.logInfo("Mechanics Registered.");
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
        MetadataManager.removeMetadata(evt.getPlayer(), Metadata.QUIT);
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
        if (MetadataManager.hasMetadata(player, Metadata.QUIT))
            return;

        MetadataManager.setMetadata(player, Metadata.QUIT, true);
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
