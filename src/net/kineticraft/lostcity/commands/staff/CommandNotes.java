package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.item.items.books.ItemBookNotes;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow writing notes on a given player.
 * Created by Kneesnap on 7/24/2017.
 */
public class CommandNotes extends StaffCommand {
    public CommandNotes() {
        super("<player>", "View stored notes about a player", "notes");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        QueryTools.getData(args[0], p -> Utils.replaceItem((Player) sender, new ItemBookNotes(p)),
                () -> sender.sendMessage(ChatColor.RED + "Player not found."));

    }
}
