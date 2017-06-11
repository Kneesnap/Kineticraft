package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Show ores nearby from a mined player.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandMined extends StaffCommand {
    public CommandMined() {
        super("<player>", "See ore mined by a player within a 100 block radius.", "mined");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        ((Player) sender).chat("/co rollback u:" + args[0] + " r:100 b:diamond_ore t:100d #preview");
        sender.sendMessage(ChatColor.GREEN + "Type '/co cancel' when done.");
    }
}
