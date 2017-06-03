package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.JsonLocation;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandSetHome - Allow players to set their homes.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandSetHome extends PlayerCommand {
    public CommandSetHome() {
        super("<name>", "Set a home.", "sethome");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        KCPlayer player = KCPlayer.getWrapper(p);

        if (p.getWorld().getEnvironment() != World.Environment.NORMAL) {
            p.sendMessage(ChatColor.RED + "You may only set homes in the overworld.");
            return;
        }

        int maxHomes = 5; //TODO
        if (player.getHomes().size() >= maxHomes) {
            p.sendMessage(ChatColor.RED + "You have reached the max number of homes for your rank.");
            return;
        }

        if (player.getHomes().containsKey(args[0])) {
            p.sendMessage(ChatColor.RED + "This home already exists. Use .delhome to remove it.");
            return;
        }

        player.getHomes().put(args[0], new JsonLocation(p.getLocation()));
        p.sendMessage(ChatColor.GRAY + "Created home '" + ChatColor.GREEN + args[0] + ChatColor.GRAY + "'.");
    }
}
