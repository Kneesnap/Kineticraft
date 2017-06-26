package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by egoscio on 6/23/17.
 */
public class CommandSee extends StaffCommand {

    public CommandSee() {
        super("<inv|end> <name>", "See a target's inventory or enderchest.", "see");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[1]);

        if (target != null) {
            switch (args[0]) {
                case "inv":
                    player.openInventory(target.getInventory());
                    player.sendMessage(ChatColor.GREEN + "Opened " + target.getName() + "'s inventory.");
                    break;
                case "end":
                    player.openInventory(target.getEnderChest());
                    player.sendMessage(ChatColor.GREEN + "Opened " + target.getName() + "'s enderchest.");
                    break;
                default:
                    showUsage(sender);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Player not found.");
        }

    }

}
