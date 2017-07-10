package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Handle players toggling things.
 * Created by Kneesnap on 7/9/2017.
 */
public class Toggles extends Mechanic {

    @Override
    public void onEnable() {
        for (Toggle t : Toggle.values())
            Commands.addCommand(new ToggleCommand(t));
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent evt) {
        Player attacker = evt.getDamager() instanceof Player ? (Player) evt.getDamager() :
                (evt.getDamager() instanceof Projectile && ((Projectile) evt.getDamager()).getShooter() instanceof Player
                ? (Player) ((Projectile) evt.getDamager()).getShooter() : null); // Yuck, maybe we can make a method for this later.
        if (attacker == null || !(evt.getEntity() instanceof Player))
            return; // One or both of the combatants isn't a player.

        boolean outgoingFail = !getToggle(attacker, Toggle.PVP);
        boolean incomingFail = !getToggle((Player) evt.getEntity(), Toggle.PVP);

        if (outgoingFail || incomingFail) {
            attacker.sendMessage(ChatColor.RED + (outgoingFail ? "You have PvP disabled. (/togglepvp)"
                    : evt.getEntity().getName() + " has PvP disabled."));
            evt.setCancelled(true);
        }
    }

    /**
     * Get the state of a toggle for a player.
     * @param player
     * @param t
     * @return state
     */
    private static boolean getToggle(Player player, Toggle t) {
        return KCPlayer.getPlayer(player).getState(t);
    }

    private class ToggleCommand extends PlayerCommand {

        private Toggle toggle;

        public ToggleCommand(Toggle t) {
            super("", "Toggle your " + t.getDescription() + ".",
                    "toggle" + t.name().toLowerCase(), t.name().toLowerCase());
            this.toggle = t;
        }

        @Override
        protected void onCommand(CommandSender sender, String[] args) {
            KCPlayer.getWrapper(sender).toggle(this.toggle);
        }
    }

    @AllArgsConstructor @Getter
    public enum Toggle {
        PVP("pvp status");

        private final EnumRank minRank;
        private String description;

        Toggle(String description) {
            this(EnumRank.MU, description);
        }
    }
}
