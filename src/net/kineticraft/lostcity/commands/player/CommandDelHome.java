package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandDelHome - Remove a home.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandDelHome extends PlayerCommand {
    public CommandDelHome() {
        super("<home>", "Remove a home.", "delhome");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        KCPlayer kcPlayer = KCPlayer.getWrapper((Player) sender);

        if (!kcPlayer.getHomes().containsKey(args[0])) {
            player.sendMessage(ChatColor.RED + "You do not have a home set with this name.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Removed home '" + args[0] + "'.");
        kcPlayer.getHomes().remove(args[0]);
    }
}
