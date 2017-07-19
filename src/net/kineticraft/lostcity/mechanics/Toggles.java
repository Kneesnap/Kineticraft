package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.stream.Collectors;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent evt) {
        List<Player> filter = evt.getRecipients().stream().filter(p -> getToggle(p, Toggle.CENSOR)).collect(Collectors.toList());
        String censored = String.format(evt.getFormat(), evt.getPlayer().getName(), evt.getMessage());
        for (String badWord : Configs.getMainConfig().getSwearWords())
            censored = censored.replaceAll("(?i)" + badWord, TextUtils.makeString('*', badWord.length()));

        evt.getRecipients().removeAll(filter);
        final String clean = censored;
        filter.forEach(p -> p.sendMessage(clean));
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
            if (this.toggle.shouldConfirm() && getToggle((Player) sender, this.toggle)) {
                sender.sendMessage(ChatColor.GRAY + "Are you sure you want to " + ChatColor.RED + "disable"
                        + ChatColor.GRAY + " the " + this.toggle.getDescription() + "?");
                Callbacks.promptConfirm((Player) sender, () -> KCPlayer.getWrapper(sender).toggle(this.toggle));
                return;
            }

            KCPlayer.getWrapper(sender).toggle(this.toggle);
        }
    }

    @AllArgsConstructor @Getter
    public enum Toggle {
        PVP("pvp status"),
        CENSOR("chat filter");

        private final EnumRank minRank;
        private String description;

        Toggle(String description) {
            this(EnumRank.MU, description);
        }

        public boolean shouldConfirm() {
            return this == CENSOR;
        }
    }
}