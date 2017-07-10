package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Mute a player.
 * Created by Kneesnap on 7/9/2017.
 */
public class CommandMute extends StaffCommand {

    public CommandMute() {
        super("<player> <time> [reason]", "Mute a player.", "mute");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        QueryTools.getData(args[0], data ->
                data.mute(sender, Utils.fromInput(args[1]), args.length > 2
                        ? String.join(" ", Utils.shift(args, 2)) : "No reason specified.")
                , () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
