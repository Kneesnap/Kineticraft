package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Unignore a player
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandUnignore extends PlayerCommand {

    public CommandUnignore() {
        super("<player>", "Allow messages from a player", "unignore");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper(sender);

        if (!player.getIgnored().containsIgnoreCase(args[0])) {
            sender.sendMessage(ChatColor.GRAY + "You are not ignoring this player.");
            return;
        }

        player.getIgnored().removeIgnoreCase(args[0]);
        sender.sendMessage(ChatColor.GRAY + "You are no longer ignoring " + ChatColor.GREEN + args[0] + ChatColor.GRAY + ".");
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.RED + "Ignored Players: " + String.join(ChatColor.GRAY + ", " + ChatColor.RED,
                KCPlayer.getWrapper(sender).getIgnored()));
    }
}