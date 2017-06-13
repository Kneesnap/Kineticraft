package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.item.items.books.ItemTPABook;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * TPA - Teleport
 *
 * Created by Kneesnap on 6/12/2017.
 */
public class CommandTPA extends PlayerCommand {

    public CommandTPA() {
        super("", "Get a teleport book.", "tpa");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Utils.giveItem((Player) sender, new ItemTPABook().generateItem());
        sender.sendMessage(ChatColor.GREEN + "Here is your teleport book.");
    }
}
