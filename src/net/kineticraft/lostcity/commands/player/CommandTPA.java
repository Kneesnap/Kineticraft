package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * TPA - Teleport to another player.
 *
 * Created by Kneesnap on 6/12/2017.
 */
public class CommandTPA extends PlayerCommand {

    public CommandTPA() {
        super("[id]", "Teleport to another player.", "tpa");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            ((Player) sender).chat("/trigger tpa set " + args[0]);
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "To " + ChatColor.RED + "tpa" + ChatColor.BLUE + ", use "
                + ChatColor.RED + "/trigger tpa set <ID> " + ChatColor.BLUE + " or " + ChatColor.RED + "/tpa <ID>"
                + ChatColor.BLUE + ".");
        sender.sendMessage(ChatColor.BLUE + "Where " + ChatColor.RED + "ID" + ChatColor.BLUE + " is their number on "
                + ChatColor.RED + "[TAB]" + ChatColor.BLUE + ".");
        sender.sendMessage(ChatColor.BLUE + "Alternatively, you can use " + ChatColor.RED + "/tpbook"
                + ChatColor.BLUE + " which makes it easier.");
    }
}
