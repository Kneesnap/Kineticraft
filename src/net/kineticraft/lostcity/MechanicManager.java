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
import java.util.Arrays;
import java.util.List;

/**
 * MechanicManager - Handles main mechanic methods.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class MechanicManager implements Listener {

    @Getter private static List<Mechanic> mechanics = new ArrayList<>();

    @Getter
    private enum Mechanics {
        CONFIGS(Configs.class),
        PLAYER_DATA(DataHandler.class),
        PUNISHMENTS(Punishments.class),
        CALLBACKS(Callbacks.class),
        COMMANDS(Commands.class),
        CRAKE(Crake.class),
        DISCORD(DiscordAPI.class, BuildType.DEV),
        RESTRICTIONS(Restrictions.class),
        SERVER_MANAGER(ServerManager.class),
        GENERAL(GeneralMechanics.class),
        VANISH(Vanish.class),
        GUIS(GUIManager.class),
        SLEEP(SleepMechanics.class),
        SLIME_FINDER(SlimeFinder.class),
        CUSTOM_ITEMS(Items.class),
        CHAT(Chat.class),
        COMPASS(CompassMechanics.class),
        FARM_LIMIT(FarmLimiter.class),
        LEASHES(Leashes.class),
        ENCHANTS(Enchants.class),
        VOTING(Voting.class),
        AFK(AFK.class),
        METADATA(MetadataManager.class);

        private final Class<? extends Mechanic> mechanicClass;
        private final List<BuildType> dontRegister;
        private Mechanic mechanic;

        Mechanics(Class<? extends Mechanic> clazz, BuildType... build) {
            this.mechanicClass = clazz;
            this.dontRegister = Arrays.asList(build);
        }

        /**
         * Is this mechanic registered and active on the server?
         * @return registered
         */
        public boolean isRegistered() {
            return getMechanic() != null;
        }

        /**
         * Register this mechanic.
         * Silently fails if it should not be applied on this server-type.
         */
        public void register() {
            if (!CONFIGS.isRegistered() || !getDontRegister().contains(Configs.getMainConfig().getBuildType()))
                addMechanic(this.mechanic = ReflectionUtil.construct(getMechanicClass()));
        }
    }

    /**
     * Register a custom mechanic.
     * @param m
     */
    public static void addMechanic(Mechanic m) {
        MechanicManager.getMechanics().add(m);
        m.onEnable();
    }

    /**
     * Registers all game mechanics.
     */
    public static void registerMechanics() {
        Core.logInfo("Registering Mechanics...");
        Bukkit.getPluginManager().registerEvents(new MechanicManager(), Core.getInstance()); // Ready to listen for event.

        // Register all mechanics here, in order of startup:
        Arrays.stream(Mechanics.values()).forEach(Mechanics::register);

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
