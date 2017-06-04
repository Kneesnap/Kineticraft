package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandHome - Teleport to your home.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandHome extends PlayerCommand {
    public CommandHome() {
        super("<home>", "Teleport home.", "home");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper((Player) sender);
        if (!player.getHomes().containsKey(args[0])) {
            showUsage(sender);
            return;
        }

        Utils.teleport((Player) sender, "Home", player.getHomes().get(args[0]).getLocation());
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.GRAY + "Homes: " + Utils.join(ChatColor.GRAY + ", ",
                KCPlayer.getWrapper((Player) sender).getHomes().keySet(), h -> ChatColor.GREEN + h));
    }
}
