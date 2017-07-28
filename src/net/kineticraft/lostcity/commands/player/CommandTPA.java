package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * TPA - Teleport to another player.
 * Created by Kneesnap on 6/12/2017.
 */
public class CommandTPA extends PlayerCommand {

    public CommandTPA() {
        super("<id>", "Teleport to another player.", "tpa", "tpahere");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        ((Player) sender).chat("/trigger " + getLastAlias() + " set " + args[0]);
    }

    @Override
    protected void showUsage(CommandSender sender) {
        String a = getLastAlias();
        sender.sendMessage(ChatColor.BLUE + "To " + ChatColor.RED + a + ChatColor.BLUE + ", use "
                + ChatColor.RED + "/trigger " + a + " set <ID> " + ChatColor.BLUE + " or " + ChatColor.RED + "/" + a + " <ID>"
                + ChatColor.BLUE + ".");
        sender.sendMessage(ChatColor.BLUE + "Where " + ChatColor.RED + "ID" + ChatColor.BLUE + " is their number on "
                + ChatColor.RED + "[TAB]" + ChatColor.BLUE + ".");

        if (a.equals("tpa"))
            sender.sendMessage(ChatColor.BLUE + "Alternatively, you can use " + ChatColor.RED + "/tpbook"
                    + ChatColor.BLUE + " which makes it easier.");
    }
}
