package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows media+ to fly.
 *
 * Created by Kneesnap on 6/11/2017.
 */
public class CommandFly extends StaffCommand {

    public CommandFly() {
        super(EnumRank.MEDIA, "", "Toggle flying.", "fly");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        p.setAllowFlight(!p.getAllowFlight());
        p.sendMessage(Utils.formatToggle("Flight", p.getAllowFlight()));
    }
}
