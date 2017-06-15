package net.kineticraft.lostcity.commands.trigger;

import net.kineticraft.lostcity.commands.TriggerCommand;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Kneesnap on 6/14/2017.
 */
public class CommandTriggerDecline extends TriggerCommand {

    public CommandTriggerDecline() {
        super("decline");
    }

    @Override
    protected void onCommand(Player sender, int value) {
        if (Callbacks.hasListener(sender, Callbacks.ListenerType.CHAT)) {
            sender.chat("cancel");
        } else {
            sender.sendMessage(ChatColor.RED + "You have nothing to decline.");
        }
    }
}
