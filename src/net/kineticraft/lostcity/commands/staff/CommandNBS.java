package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Play a sound-file.
 * Created by Kneesnap on 8/3/2017.
 */
public class CommandNBS extends StaffCommand {

    public CommandNBS() {
        super("[sound] [repeat]", "Play a note-block sound file.", "nbs");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            Utils.stopNBS(p);
            sender.sendMessage(ChatColor.GREEN + "Sounds fading out.");
            return;
        }

        Utils.playNBS(Arrays.asList(p), args[0], isArg(args, 1, "repeat"));
    }
}