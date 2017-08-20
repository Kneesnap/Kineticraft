package net.kineticraft.lostcity.commands.trigger;

import net.kineticraft.lostcity.commands.TriggerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Allows teleporting to players.
 * Created by Kneesnap on 6/14/2017.
 */
public class CommandTPATrigger extends TriggerCommand {

    public CommandTPATrigger() {
        super("tpa");
    }

    @Override
    protected void onCommand(Player player, int value) {
        KCPlayer other = KCPlayer.getById(value);
        KCPlayer sender = KCPlayer.getWrapper(player);

        if (!Utils.isVisible(player, other))
            return;

        Player receiver = other.getPlayer();
        player.sendMessage(ChatColor.GOLD + "Request sent!");
        if (KCPlayer.getWrapper(receiver).isIgnoring(player))
            return;

        receiver.sendMessage(ChatColor.GOLD + "Incoming TPA request from " + sender.getDisplayName());

        Callbacks.promptConfirm(receiver, () -> {
            if (!player.isOnline()) {
                receiver.sendMessage(ChatColor.RED + "The requestor has gone offline.");
                return;
            }

            receiver.sendMessage(ChatColor.GOLD + "Request accepted.");
            Utils.teleport(player, other.getDisplayName(), receiver.getLocation());
        }, () -> receiver.sendMessage(ChatColor.GOLD + "Request denied."), "Accept", "Decline");
    }
}
