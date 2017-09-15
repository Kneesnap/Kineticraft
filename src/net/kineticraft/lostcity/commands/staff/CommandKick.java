package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
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
        String reason = args.length > 1 ? String.join(" ", Utils.shift(args)) : "Kicked by an operator.";
        p.kickPlayer(reason);
        sender.sendMessage("Kicked " + p.getName() + (reason != null && reason.length() > 0 ? " for " + reason : "") + ".");
        DiscordAPI.sendMessage(DiscordChannel.ORYX, sender.getName() + " kicked " + p.getName() + " for ``" + reason + "``.");
    }
}
