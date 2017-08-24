package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows staff to fly.
 * Created by Kneesnap on 8/4/2017.
 */
public class CommandFly extends StaffCommand {
    public CommandFly() {
        super(EnumRank.MEDIA, "", "Toggle flight-status.", "fly");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        p.setAllowFlight(!p.getAllowFlight());
        sender.sendMessage(Utils.formatToggle("Flight", p.getAllowFlight()));
    }
}
