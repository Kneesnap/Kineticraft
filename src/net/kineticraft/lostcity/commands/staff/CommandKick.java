package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow helpers to kick players.
 * Created by Kneesnap on 7/10/2017.
 */
public class CommandKick extends StaffCommand {

    public CommandKick() {
        super("<player> [reason]", "Kick a player", "kick");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (!Utils.isVisible(sender, args[0]))
            return;

        Player p = Bukkit.getPlayer(args[0]);
        String reason = String.join(" ", Utils.shift(args));
        p.kickPlayer("Kicked by an operator:\n" + reason);
        sender.sendMessage("Kicked " + p.getName() + (reason != null && reason.length() > 0 ? " for " + reason : "") + ".");
    }
}
