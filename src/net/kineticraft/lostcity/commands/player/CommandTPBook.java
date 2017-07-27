package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.item.items.books.ItemTPABook;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Spawn a teleport book.
 * Created by Kneesnap on 6/14/2017.
 */
public class CommandTPBook extends PlayerCommand {
    public CommandTPBook() {
        super("", "Spawn a teleport book.", "tpbook", "tpabook");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Utils.replaceItem((Player) sender, new ItemTPABook());
        sender.sendMessage(ChatColor.GREEN + "TPA book spawned.");
    }
}
