package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.Chat;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandSetHome - Allow players to set their homes.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandSetHome extends PlayerCommand {
    public CommandSetHome() {
        super("<name>", "Set a home.", "sethome");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        KCPlayer kcPlayer = KCPlayer.getWrapper(player);

        if (!player.getWorld().equals(Core.getMainWorld())) {
            sender.sendMessage(ChatColor.RED + "You may only set homes in the overworld.");
            return;
        }

        if (args[0].equalsIgnoreCase("bed") || Chat.isObscene(args[0])) {
            sender.sendMessage(ChatColor.RED + "This home name is not permitted.");
            return;
        }

        if (kcPlayer.getHomes().size() >= kcPlayer.getRank().getHomes()) {
            sender.sendMessage(ChatColor.RED + "You have reached the max number of homes for your rank.");
            return;
        }

        if (kcPlayer.getHomes().containsKey(args[0])) {
            sender.sendMessage(ChatColor.RED + "This home already exists. Use /delhome to remove it.");
            return;
        }

        kcPlayer.getHomes().put(args[0], player.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created home '" + ChatColor.GREEN + args[0] + ChatColor.GRAY + "'.");
    }
}
