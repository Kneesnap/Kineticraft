package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Play a sound-file.
 * Created by Kneesnap on 8/3/2017.
 */
public class CommandNBS extends StaffCommand {

    public CommandNBS() {
        super("<sound>", "Play a note-block sound file.", "nbs");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Utils.playSound(Arrays.asList((Player) sender), args[0]);
    }
}
