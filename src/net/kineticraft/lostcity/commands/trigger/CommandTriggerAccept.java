package net.kineticraft.lostcity.commands.trigger;

import net.kineticraft.lostcity.commands.TriggerCommand;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Trigger command to accept a prompt.
 * Created by Kneesnap on 6/14/2017.
 */
public class CommandTriggerAccept extends TriggerCommand {

    public CommandTriggerAccept() {
        super("accept");
    }

    @Override
    protected void onCommand(Player sender, int value) {
        if (Callbacks.hasListener(sender, Callbacks.ListenerType.TRIGGER)) {
            Callbacks.accept(sender, Callbacks.ListenerType.TRIGGER, null);
        } else {
            sender.sendMessage(ChatColor.RED + "You have nothing to accept.");
        }
    }
}
