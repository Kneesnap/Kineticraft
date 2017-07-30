package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport to your last location.
 * Created by Kneesnap on 7/29/2017.
 */
public class CommandBack extends StaffCommand {
    public CommandBack() {
        super("", "Teleport to your last location.", "back");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Utils.teleport((Player) sender, "Last Location", KCPlayer.getPlayer((Player) sender).getLastLocation());
    }
}
