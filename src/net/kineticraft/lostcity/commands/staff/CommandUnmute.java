package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Handle unmuting of players.
 * Created by Kneesnap on 7/9/2017.
 */
public class CommandUnmute extends StaffCommand {

    public CommandUnmute() {
        super("<player>", "Unmute a player.", "unmute");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        QueryTools.getData(args[0], data -> {
            if (!data.isMuted()) {
                sender.sendMessage(ChatColor.RED + data.getUsername() + " is not muted.");
                return;
            }

            data.setMute(null);
            sender.sendMessage(ChatColor.GOLD + "Unmuted " + data.getUsername());
            if (data.isOnline())
                data.getPlayer().sendMessage(ChatColor.GREEN + "You have been unmuted.");
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}