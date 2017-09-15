package net.kineticraft.lostcity.party;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.File;

/**
 * Handles the wondrous Kineticraft Parties.
 * Created by Kneesnap on 9/14/2017.
 */
@Restrict(BuildType.PRODUCTION)
public class Parties extends Mechanic {
    @Getter private static Party party; // Null = no party.
    @Getter private static World partyWorld;

    @Override
    public void onEnable() {
        partyWorld = new WorldCreator("party_world").seed(1000000).generateStructures(false).createWorld(); // Load party-world.

        if (isPartyTime())
            Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () ->
                Bukkit.broadcastMessage(ChatColor.AQUA + "There is a party going on! Do " + ChatColor.YELLOW + "/party" + ChatColor.AQUA + " to attend!"), 0L, 6000L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onElytraToggle(EntityToggleGlideEvent evt) { // Disables elytra in party world.
        evt.setCancelled(evt.isGliding() && evt.getEntity().getWorld().equals(getPartyWorld()));
    }

    @Override // Teleport players out on quit, prevents logging in at bad coordinates.
    public void onQuit(Player player) {
        if (player.getWorld().equals(getPartyWorld()))
            Utils.toSpawn(player);
    }

    @Override
    public void onJoin(Player player) {
        if (isPartyTime())
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), ()
                    -> player.sendMessage(ChatColor.AQUA + "There is a party going on! Do " + ChatColor.YELLOW + "/party" + ChatColor.AQUA + " to attend!"), 100);
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
     * Is there an active party?
     * @return isPartyTime
     */
    public static boolean isPartyTime() {
        return getParty() != null;
    }
}
